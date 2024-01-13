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

package org.jraf.libticker.plugin.api

interface PluginDescriptorProvider {
    val pluginDescriptor: PluginDescriptor
}

data class PluginDescriptor(
    val className: String,
    val displayName: String,
    val moreInfo: String? = null,
    val configurationDescriptor: PluginConfigurationDescriptor? = null
)

data class PluginConfigurationDescriptor(
    val itemDescriptors: List<PluginConfigurationItemDescriptor>,
    val moreInfo: String? = null
)

data class PluginConfigurationItemDescriptor(
    val key: String,
    val type: PluginConfigurationItemType,
    val displayName: String,
    val moreInfo: String? = null,
    val defaultValue: String? = null,
    val required: Boolean = true
)

sealed class PluginConfigurationItemType {
    object StringType : PluginConfigurationItemType()
    object NumberType : PluginConfigurationItemType()
    object BooleanType : PluginConfigurationItemType()
    class ChoiceType(vararg val choices: String) : PluginConfigurationItemType()
}
