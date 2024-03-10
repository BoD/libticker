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

package org.jraf.libticker.plugin.manager

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.double
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlinx.serialization.json.longOrNull
import org.jraf.libticker.message.MessageQueue
import org.jraf.libticker.plugin.api.Configuration
import org.jraf.libticker.plugin.api.Plugin
import org.jraf.libticker.plugin.api.PluginDescriptor
import org.jraf.libticker.plugin.api.PluginDescriptorProvider
import java.util.ServiceLoader

class PluginManager(private val messageQueue: MessageQueue) {
    private val _managedPlugins = mutableListOf<Plugin>()
    private val _managedPluginsChanged = MutableStateFlow<String?>(null)
    val managedPluginsChanged: Flow<String> = _managedPluginsChanged.filterNotNull()
    val globalConfiguration: Configuration by lazy { Configuration() }

    fun managePlugin(
        pluginClassName: String,
        pluginConfiguration: Configuration,
        notifyListeners: Boolean = false
    ) {
        val plugin = (Class.forName(pluginClassName).getDeclaredConstructor().newInstance() as Plugin)
        _managedPlugins += plugin
        plugin.init(messageQueue, pluginConfiguration, globalConfiguration)
        plugin.start()

        if (notifyListeners) notifyListeners()
    }

    fun managePlugins(jsonString: String, notifyListeners: Boolean = false) {
        val managePluginsJsonArray = Json.parseToJsonElement(jsonString).jsonArray
        for (pluginJsonObject in managePluginsJsonArray) {
            pluginJsonObject as JsonObject
            val pluginClassName = pluginJsonObject.get(JSON_CLASS_NAME)!!.jsonPrimitive.content
            val configuration = pluginJsonObject.get(JSON_CONFIGURATION)?.jsonObject?.let { configurationJsonObject ->
                Configuration(
                    *configurationJsonObject.entries.map { (key: String, value: JsonElement) ->
                        val jsonPrimitive = value.jsonPrimitive
                        // Unfortunately, https://github.com/Kotlin/kotlinx.serialization/issues/1298
                        key to when {
                            jsonPrimitive.isString -> jsonPrimitive.content
                            jsonPrimitive.booleanOrNull != null -> jsonPrimitive.boolean
                            jsonPrimitive.doubleOrNull != null -> jsonPrimitive.double
                            jsonPrimitive.longOrNull != null -> jsonPrimitive.long
                            else -> error("Unsupported type: ${value::class.simpleName}")
                        }
                    }.toTypedArray()
                )
            } ?: Configuration()
            managePlugin(pluginClassName, configuration)
        }
        if (notifyListeners) notifyListeners()
    }

    fun unmanagePlugin(plugin: Plugin, notifyListeners: Boolean = false) {
        if (plugin.isRunning) plugin.stop()
        _managedPlugins -= plugin
        messageQueue.unset(plugin)
        if (notifyListeners) notifyListeners()
    }

    fun startAllManagedPlugins() {
        for (plugin in _managedPlugins) {
            if (!plugin.isRunning) plugin.start()
        }
    }

    fun stopAllManagedPlugins() {
        for (plugin in _managedPlugins) {
            if (plugin.isRunning) plugin.stop()
        }
    }

    private fun notifyListeners() {
        _managedPluginsChanged.value = getManagedPluginsAsJsonString()
    }

    val managedPlugins: List<Plugin> = _managedPlugins

    val availablePlugins: List<PluginDescriptor> by lazy {
        ServiceLoader.load(PluginDescriptorProvider::class.java).map { it.pluginDescriptor }
    }

    fun getManagedPluginsAsJsonString(): String {
        return Json.encodeToString(
            JsonArray(
                _managedPlugins.map { plugin ->
                    JsonObject(
                        mapOf(
                            JSON_CLASS_NAME to JsonPrimitive(plugin.descriptor.className),
                            JSON_CONFIGURATION to JsonObject(
                                plugin.pluginConfiguration.asMap.mapValues {
                                    val value = it.value
                                    when (value) {
                                        is Number -> JsonPrimitive(value)
                                        is Boolean -> JsonPrimitive(value)
                                        is String -> JsonPrimitive(value)
                                        else -> error("Unsupported type: ${value::class.simpleName}")
                                    }
                                }
                            )
                        )
                    )
                }
            )
        )
    }

    companion object {
        private const val JSON_CLASS_NAME = "className"
        private const val JSON_CONFIGURATION = "configuration"
    }
}
