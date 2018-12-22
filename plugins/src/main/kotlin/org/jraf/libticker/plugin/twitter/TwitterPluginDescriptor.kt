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
    val DESCRIPTOR = PluginDescriptor(
        className = "${this::class.java.`package`.name}.TwitterPlugin",
        displayName = "Twitter",
        configurationDescriptor = PluginConfigurationDescriptor(
            listOf(
                PluginConfigurationItemDescriptor(
                    key = "oAuthConsumerKey",
                    type = PluginConfigurationItemType.STRING,
                    displayName = "OAuth consumer key"
                ),
                PluginConfigurationItemDescriptor(
                    key = "oAuthConsumerSecret",
                    type = PluginConfigurationItemType.STRING,
                    displayName = "OAuth consumer secret"
                ),
                PluginConfigurationItemDescriptor(
                    key = "oAuthAccessToken",
                    type = PluginConfigurationItemType.STRING,
                    displayName = "OAuth access token"
                ),
                PluginConfigurationItemDescriptor(
                    key = "oAuthAccessTokenSecret",
                    type = PluginConfigurationItemType.STRING,
                    displayName = "OAuth access token secret"
                )
            )
        )
    )
}

class TwitterPluginDescriptorProvider : PluginDescriptorProvider {
    override val pluginDescriptor = TwitterPluginDescriptor.DESCRIPTOR
}