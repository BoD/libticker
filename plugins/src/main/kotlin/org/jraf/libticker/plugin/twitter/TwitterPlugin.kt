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

package org.jraf.libticker.plugin.twitter

import org.jraf.libticker.message.Message
import org.jraf.libticker.message.MessageQueue
import org.jraf.libticker.plugin.api.Configuration
import org.jraf.libticker.plugin.base.BasePlugin
import org.jraf.libticker.plugin.twitter.TwitterPluginDescriptor.KEY_OAUTH_ACCESS_TOKEN
import org.jraf.libticker.plugin.twitter.TwitterPluginDescriptor.KEY_OAUTH_ACCESS_TOKEN_SECRET
import org.jraf.libticker.plugin.twitter.TwitterPluginDescriptor.KEY_OAUTH_CONSUMER_KEY
import org.jraf.libticker.plugin.twitter.TwitterPluginDescriptor.KEY_OAUTH_CONSUMER_SECRET
import org.jraf.libticker.plugin.twitter.TwitterPluginDescriptor.KEY_SEARCH
import twitter4j.Status
import java.util.ResourceBundle
import java.util.regex.Pattern

class TwitterPlugin : BasePlugin() {
    override val descriptor = TwitterPluginDescriptor.DESCRIPTOR

    private lateinit var twitterClient: TwitterClient

    val resourceBundle: ResourceBundle by lazy {
        ResourceBundle.getBundle(javaClass.name)
    }

    override fun init(
        messageQueue: MessageQueue,
        pluginConfiguration: Configuration,
        globalConfiguration: Configuration
    ) {
        super.init(messageQueue, pluginConfiguration, globalConfiguration)
        twitterClient = TwitterClient(
            pluginConfiguration.getString(KEY_OAUTH_CONSUMER_KEY),
            pluginConfiguration.getString(KEY_OAUTH_CONSUMER_SECRET),
            pluginConfiguration.getString(KEY_OAUTH_ACCESS_TOKEN),
            pluginConfiguration.getString(KEY_OAUTH_ACCESS_TOKEN_SECRET),
            Search.parse(pluginConfiguration.getString(KEY_SEARCH))
        )
    }

    override fun start() {
        super.start()
        twitterClient.addListener(object : StatusListener {
            override fun onNewStatuses(statuses: List<Status>) {
                for (status in statuses) {
                    val screenName = status.user.screenName
                    var statusText = status.text

                    // Remove all urls
                    for (urlEntity in status.urlEntities) {
                        statusText = statusText.replace(Pattern.quote(urlEntity.url).toRegex(), "")
                    }
                    for (mediaEntity in status.mediaEntities) {
                        statusText = statusText.replace(Pattern.quote(mediaEntity.url).toRegex(), "")
                    }

                    // Remove trailing ':' (usually left over after removing urls)
                    statusText = statusText.trim()
                    if (statusText.endsWith(":")) {
                        statusText = statusText.substring(0, statusText.length - 1)
                    }

                    // Add author and format
                    val textPlain = resourceBundle.getString("status_plain").format(screenName, statusText)
                    val textFormatted = resourceBundle.getString("status_formatted").format(screenName, statusText)

                    messageQueue += Message(
                        text = textPlain,
                        textFormatted = textFormatted,
                        uri = "https://twitter.com/$screenName/status/${status.id}",
                        imageUri = status.mediaEntities.firstOrNull { it.type == "photo" }?.mediaURLHttps,
                        hints = mapOf("image.cropAllowed" to "true")
                    )
                }
            }
        })

        twitterClient.startClient()
    }

    override fun stop() {
        twitterClient.stopClient()
        super.stop()
    }
}