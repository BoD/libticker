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

package org.jraf.libticker.plugin.googlephotos

import org.jraf.libticker.plugin.api.PluginConfigurationDescriptor
import org.jraf.libticker.plugin.api.PluginConfigurationItemDescriptor
import org.jraf.libticker.plugin.api.PluginConfigurationItemType
import org.jraf.libticker.plugin.api.PluginDescriptor
import org.jraf.libticker.plugin.api.PluginDescriptorProvider

object GooglePhotosPluginDescriptor {
    const val KEY_PERIOD = "period"
    const val KEY_CLIENT_ID = "clientId"
    const val KEY_CLIENT_SECRET = "clientSecret"
    const val KEY_REFRESH_TOKEN = "refreshToken"

    val DESCRIPTOR = PluginDescriptor(
        className = GooglePhotosPlugin::class.java.name,
        displayName = "Google Photos",
        configurationDescriptor = PluginConfigurationDescriptor(
            listOf(
                PluginConfigurationItemDescriptor(
                    key = KEY_PERIOD,
                    type = PluginConfigurationItemType.NumberType,
                    displayName = "Period",
                    moreInfo = "in minutes",
                    defaultValue = "2"
                ),
                PluginConfigurationItemDescriptor(
                    key = KEY_CLIENT_ID,
                    type = PluginConfigurationItemType.StringType,
                    displayName = "Client id"
                ),
                PluginConfigurationItemDescriptor(
                    key = KEY_CLIENT_SECRET,
                    type = PluginConfigurationItemType.StringType,
                    displayName = "Client secret"
                ),
                PluginConfigurationItemDescriptor(
                    key = KEY_REFRESH_TOKEN,
                    type = PluginConfigurationItemType.StringType,
                    displayName = "Refresh token"
                )
            ),
            moreInfo = """To get the keys, please visit
                | <a href="https://developers.google.com/photos/library/guides/get-started" target="_blank">https://developers.google.com/photos/library/guides/get-started</a>.
                | """.trimMargin()
        )
    )
}

class GooglePhotosPluginDescriptorProvider : PluginDescriptorProvider {
    override val pluginDescriptor = GooglePhotosPluginDescriptor.DESCRIPTOR
}