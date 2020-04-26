/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2018 Benoit 'BoD' Lubek (BoD@JRAF.org)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jraf.libticker.plugin.googlephotos

import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.UserCredentials
import com.google.photos.library.v1.PhotosLibraryClient
import com.google.photos.library.v1.PhotosLibrarySettings
import com.google.photos.types.proto.Album
import com.google.photos.types.proto.MediaItem
import org.jraf.libticker.message.Message
import org.jraf.libticker.message.MessageQueue
import org.jraf.libticker.plugin.api.Configuration
import org.jraf.libticker.plugin.base.PeriodicPlugin
import org.jraf.libticker.plugin.googlephotos.GooglePhotosPluginDescriptor.KEY_PERIOD
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.random.Random


class GooglePhotosPlugin : PeriodicPlugin() {
    override val descriptor = GooglePhotosPluginDescriptor.DESCRIPTOR
    override val periodMs get() = TimeUnit.MINUTES.toMillis(pluginConfiguration.getNumber(KEY_PERIOD).toLong())

    private lateinit var photosLibraryClient: PhotosLibraryClient
    private val allAlbums = mutableListOf<Album>()
    private var allAlbumsRetrieveDate = Date(0)

    override fun init(
        messageQueue: MessageQueue,
        pluginConfiguration: Configuration,
        globalConfiguration: Configuration
    ) {
        super.init(messageQueue, pluginConfiguration, globalConfiguration)
        val userCredentials = UserCredentials.newBuilder()
            .setClientId(pluginConfiguration.getString(GooglePhotosPluginDescriptor.KEY_CLIENT_ID))
            .setClientSecret(pluginConfiguration.getString(GooglePhotosPluginDescriptor.KEY_CLIENT_SECRET))
            .setRefreshToken(pluginConfiguration.getString(GooglePhotosPluginDescriptor.KEY_REFRESH_TOKEN))
            .build()

        val photosLibrarySettings = PhotosLibrarySettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(userCredentials))
            .build()

        photosLibraryClient = PhotosLibraryClient.initialize(photosLibrarySettings)
    }

    private fun getOrFetchAllAlbums(): List<Album> {
        if (allAlbumsRetrieveDate.before(todayAt0Hours())) {
            allAlbums.clear()
            for (album in photosLibraryClient.listAlbums().iterateAll()) {
                allAlbums += album
            }
        }
        return allAlbums
    }

    private fun getRandomPhoto(retryCount: Int = 0): MediaItem? {
        if (retryCount >= 3) {
            LOGGER.warn("Could not get a random photo after $retryCount retries")
            return null
        }
        val albumList = getOrFetchAllAlbums()
        val totalMediaCount = albumList.sumBy { it.mediaItemsCount.toInt() }
        val randomIndex = Random.nextInt(totalMediaCount)
        var randomIndexRemainingForAlbum = randomIndex
        var total = 0L
        var albumContainingRandomPhoto: Album? = null
        for (album in albumList) {
            total += album.mediaItemsCount
            if (randomIndex < total) {
                albumContainingRandomPhoto = album
                break
            }
            randomIndexRemainingForAlbum -= album.mediaItemsCount.toInt()
        }

        for ((index, mediaItem) in photosLibraryClient.searchMediaItems(albumContainingRandomPhoto!!.id).iterateAll()
            .withIndex()) {
            if (index == randomIndexRemainingForAlbum) {
                val mimeType = mediaItem.mimeType.toLowerCase(Locale.US)
                if (!mimeType.contains("jpg") && !mimeType.contains("jpeg")) {
                    // Ignore videos
                    LOGGER.info("Random media is not a photo: try another one")
                    return getRandomPhoto(retryCount + 1)
                }
                return mediaItem
            }
        }
        return null
    }

    override fun queueMessage() {
        val randomPhoto = try {
            getRandomPhoto()
        } catch (t: Throwable) {
            LOGGER.warn("Could not fetch a random photo: give up", t)
            return
        } ?: return
        val displayWidth = globalConfiguration.getNumberOrNull("displayWidth") ?: 1024
        val displayHeight = globalConfiguration.getNumberOrNull("displayHeight") ?: 768
        val photoUrl = randomPhoto.baseUrl + "=w$displayWidth-h$displayHeight-c"
        val takenDate = Date(randomPhoto.mediaMetadata.creationTime.seconds * 1000)
        messageQueue[this] = Message(
            text = "Taken on ${DATE_FORMATTER.format(takenDate)}",
            textFormatted = "Picture taken on<br>${DATE_FORMATTER.format(takenDate)}",
            imageUri = photoUrl,
            hints = mapOf(
                "image.cropAllowed" to "true",
                "image.displayDuration" to "long"
            )
        )
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(GooglePhotosPlugin::class.java)

        private val DATE_FORMATTER = SimpleDateFormat("MMMM\u00A0dd, yyyy", Locale.US)

        private fun todayAt0Hours(): Date = Calendar.getInstance().apply {
            this[Calendar.HOUR_OF_DAY] = 0
            this[Calendar.MINUTE] = 0
            this[Calendar.SECOND] = 0
        }.time
    }
}

// Run this to get your refresh token
fun main() {
    val clientId = "200586986744-9c4qkqc87je1mc2h1474dlvm9k3pqpc9.apps.googleusercontent.com"
    val clientSecret = "replace me with the client secret!"
    val redirectUrl = "http://localhost"
    println(
        GoogleAuthorizationCodeRequestUrl(
            clientId,
            redirectUrl,
            listOf("https://www.googleapis.com/auth/photoslibrary.readonly")
        ).setAccessType("offline")
            .build()
    )

    println("Enter the redirected url")
    val redirectedUrl = readLine()

    val authorizationCode = AuthorizationCodeResponseUrl(redirectedUrl).code
    val googleTokenResponse = GoogleAuthorizationCodeTokenRequest(
        NetHttpTransport(),
        JacksonFactory(),
        clientId,
        clientSecret,
        authorizationCode,
        redirectUrl
    )
        .execute()

    println("refreshToken=${googleTokenResponse.refreshToken}")
}
