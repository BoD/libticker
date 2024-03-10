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

package org.jraf.libticker.plugin.weather

import org.jraf.libticker.plugin.api.PluginConfigurationDescriptor
import org.jraf.libticker.plugin.api.PluginConfigurationItemDescriptor
import org.jraf.libticker.plugin.api.PluginConfigurationItemType
import org.jraf.libticker.plugin.api.PluginDescriptor
import org.jraf.libticker.plugin.api.PluginDescriptorProvider

object WeatherPluginDescriptor {
    const val KEY_JWT_TOKEN = "apiKey"
    const val KEY_FORMATTING_LOCALE = "formattingLocale"
    const val KEY_PERIOD = "period"

    val DESCRIPTOR = PluginDescriptor(
        className = WeatherPlugin::class.java.name,
        displayName = "Weather",
        configurationDescriptor = PluginConfigurationDescriptor(
            listOf(
                PluginConfigurationItemDescriptor(
                    key = KEY_JWT_TOKEN,
                    type = PluginConfigurationItemType.StringType,
                    displayName = "WeatherKit JWT Token"
                ),
                PluginConfigurationItemDescriptor(
                    key = KEY_FORMATTING_LOCALE,
                    type = PluginConfigurationItemType.StringType,
                    displayName = "Date locale",
                    defaultValue = "en",
                    required = false
                ),
                PluginConfigurationItemDescriptor(
                    key = KEY_PERIOD,
                    type = PluginConfigurationItemType.NumberType,
                    displayName = "Period",
                    moreInfo = "in minutes",
                    defaultValue = "10"
                )
            )
        )
    )
}

class WeatherPluginDescriptorProvider : PluginDescriptorProvider {
    override val pluginDescriptor = WeatherPluginDescriptor.DESCRIPTOR
}
