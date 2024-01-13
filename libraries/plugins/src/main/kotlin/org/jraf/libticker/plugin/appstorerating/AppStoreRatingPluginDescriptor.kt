/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2019-present Benoit 'BoD' Lubek (BoD@JRAF.org)
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

package org.jraf.libticker.plugin.appstorerating

import org.jraf.libticker.plugin.api.PluginConfigurationDescriptor
import org.jraf.libticker.plugin.api.PluginConfigurationItemDescriptor
import org.jraf.libticker.plugin.api.PluginConfigurationItemType
import org.jraf.libticker.plugin.api.PluginDescriptor
import org.jraf.libticker.plugin.api.PluginDescriptorProvider

object AppStoreRatingPluginDescriptor {
    const val KEY_STORE = "store"
    const val KEY_APP_ID = "appId"
    const val KEY_TITLE = "title"
    const val KEY_PERIOD = "period"

    const val KEY_STORE_ANDROID_PLAY_STORE = "Android Play Store"
    const val KEY_STORE_IOS_APP_STORE = "iOS App Store"

    val DESCRIPTOR = PluginDescriptor(
        className = AppStoreRatingPlugin::class.java.name,
        displayName = "Android and iOS app stores ratings",
        configurationDescriptor = PluginConfigurationDescriptor(
            listOf(
                PluginConfigurationItemDescriptor(
                    key = KEY_STORE,
                    type = PluginConfigurationItemType.ChoiceType(
                        KEY_STORE_ANDROID_PLAY_STORE,
                        KEY_STORE_IOS_APP_STORE
                    ),
                    displayName = "Store",
                    defaultValue = KEY_STORE_ANDROID_PLAY_STORE
                ),
                PluginConfigurationItemDescriptor(
                    key = KEY_APP_ID,
                    type = PluginConfigurationItemType.StringType,
                    displayName = "App Id",
                    moreInfo = "e.g. com.example.myapp (Android) or id402167427 (iOS)"

                ),
                PluginConfigurationItemDescriptor(
                    key = KEY_TITLE,
                    type = PluginConfigurationItemType.StringType,
                    displayName = "Title",
                    moreInfo = "e.g. the app's name"
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

class AppStoreRatingPluginDescriptorProvider : PluginDescriptorProvider {
    override val pluginDescriptor = AppStoreRatingPluginDescriptor.DESCRIPTOR
}