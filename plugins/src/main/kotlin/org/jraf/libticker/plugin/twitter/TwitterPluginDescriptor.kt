/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2018-present Benoit 'BoD' Lubek (BoD@JRAF.org)
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

import org.jraf.libticker.plugin.api.PluginConfigurationDescriptor
import org.jraf.libticker.plugin.api.PluginConfigurationItemDescriptor
import org.jraf.libticker.plugin.api.PluginConfigurationItemType
import org.jraf.libticker.plugin.api.PluginDescriptor
import org.jraf.libticker.plugin.api.PluginDescriptorProvider

object TwitterPluginDescriptor {
    const val KEY_OAUTH_CONSUMER_KEY = "oAuthConsumerKey"
    const val KEY_OAUTH_CONSUMER_SECRET = "oAuthConsumerSecret"
    const val KEY_OAUTH_ACCESS_TOKEN = "oAuthAccessToken"
    const val KEY_OAUTH_ACCESS_TOKEN_SECRET = "oAuthAccessTokenSecret"
    const val KEY_SEARCH = "search"

    val DESCRIPTOR = PluginDescriptor(
        className = "${this::class.java.`package`.name}.TwitterPlugin",
        displayName = "Twitter",
        configurationDescriptor = PluginConfigurationDescriptor(
            listOf(
                PluginConfigurationItemDescriptor(
                    key = KEY_OAUTH_CONSUMER_KEY,
                    type = PluginConfigurationItemType.STRING,
                    displayName = "OAuth consumer key"
                ),
                PluginConfigurationItemDescriptor(
                    key = KEY_OAUTH_CONSUMER_SECRET,
                    type = PluginConfigurationItemType.STRING,
                    displayName = "OAuth consumer secret"
                ),
                PluginConfigurationItemDescriptor(
                    key = KEY_OAUTH_ACCESS_TOKEN,
                    type = PluginConfigurationItemType.STRING,
                    displayName = "OAuth access token"
                ),
                PluginConfigurationItemDescriptor(
                    key = KEY_OAUTH_ACCESS_TOKEN_SECRET,
                    type = PluginConfigurationItemType.STRING,
                    displayName = "OAuth access token secret"
                ),
                PluginConfigurationItemDescriptor(
                    key = KEY_SEARCH,
                    type = PluginConfigurationItemType.STRING,
                    displayName = "Search",
                    moreInfo = """Words to search (for instance: <i>olympics</i>), or a list name in the form <b>list:userName/listName</b> (for instance: <i>list:bod/news</i>)"""
                )
            ),
            moreInfo = """To get the oAuth keys, please visit
                | <a href="https://developer.twitter.com/en/apps" target="_blank">https://developer.twitter.com/en/apps</a>
                | and register your application.""".trimMargin()
        )
    )
}

class TwitterPluginDescriptorProvider : PluginDescriptorProvider {
    override val pluginDescriptor = TwitterPluginDescriptor.DESCRIPTOR
}