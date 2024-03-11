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

package org.jraf.libticker.plugin.bingimages

import kotlinx.serialization.Serializable
import org.jraf.libticker.message.Message
import org.jraf.libticker.message.MessageQueue
import org.jraf.libticker.plugin.api.Configuration
import org.jraf.libticker.plugin.base.PeriodicPlugin
import org.jraf.libticker.plugin.bingimages.BingImagesPluginDescriptor.KEY_PERIOD
import org.jraf.libticker.plugin.util.fetch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

class BingImagesPlugin : PeriodicPlugin() {
    override val descriptor = BingImagesPluginDescriptor.DESCRIPTOR
    override val periodMs get() = TimeUnit.MINUTES.toMillis(pluginConfiguration.getNumber(KEY_PERIOD).toLong())

    private lateinit var queue: ArrayDeque<String>
    private var queueSize: Int = 0
    private var currentImage: String = ""

    override fun init(
        messageQueue: MessageQueue,
        pluginConfiguration: Configuration,
        globalConfiguration: Configuration,
    ) {
        super.init(messageQueue, pluginConfiguration, globalConfiguration)
        queueSize = pluginConfiguration.getNumber(BingImagesPluginDescriptor.KEY_QUEUE_SIZE).toInt()
        queue = ArrayDeque(queueSize)
    }

    override fun queueMessage() {
        val images = fetch<List<JsonImage>>("https://peapix.com/bing/feed").map { it.fullUrl }
        val newImages = images.filter { it !in queue }
        for (newImage in newImages.reversed()) {
            queue.addFirst(newImage)
        }
        while (queue.size > queueSize) {
            queue.removeLast()
        }
        if (currentImage.isEmpty()) {
            currentImage = queue.first()
        } else {
            val index = queue.indexOf(currentImage)
            if (index == -1) {
                currentImage = queue.first()
            } else {
                currentImage = queue.elementAt((index + 1) % queue.size)
            }
        }
        LOGGER.debug("image: $currentImage")

        messageQueue *= Message(
            text = "",
            imageUri = currentImage,
            hints = mapOf(
                "image.wallpaper" to "true",
            )
        )
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(BingImagesPlugin::class.java)
    }
}

@Serializable
data class JsonImage(
    val fullUrl: String,
)
