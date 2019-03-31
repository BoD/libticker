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

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.json
import io.reactivex.Flowable
import io.reactivex.processors.PublishProcessor
import org.jraf.libticker.message.MessageQueue
import org.jraf.libticker.plugin.api.Plugin
import org.jraf.libticker.plugin.api.PluginConfiguration
import org.jraf.libticker.plugin.api.PluginDescriptor
import org.jraf.libticker.plugin.api.PluginDescriptorProvider
import java.util.ServiceLoader

class PluginManager(private val messageQueue: MessageQueue) {
    private val _managedPlugins = mutableListOf<Plugin>()
    private val _managedPluginsChanged = PublishProcessor.create<String>().toSerialized()
    val managedPluginsChanged: Flowable<String> = _managedPluginsChanged.hide()

    fun managePlugin(
        pluginClassName: String,
        configuration: PluginConfiguration,
        notifyListeners: Boolean = false
    ) {
        val plugin = (Class.forName(pluginClassName).newInstance() as Plugin)
        _managedPlugins += plugin
        plugin.init(messageQueue, configuration)
        plugin.start()
        if (notifyListeners) notifyListeners()
    }

    fun managePlugins(jsonString: String, notifyListeners: Boolean = false) {
        val parser: Parser = Parser.default()
        val managePluginsJsonArray = parser.parse(StringBuilder(jsonString)) as JsonArray<*>
        for (pluginJsonObject in managePluginsJsonArray) {
            if (pluginJsonObject !is JsonObject) continue
            val pluginClassName = pluginJsonObject.string(JSON_CLASS_NAME)!!
            val configuration = pluginJsonObject.obj(JSON_CONFIGURATION)?.let { configurationJsonObject ->
                PluginConfiguration(*configurationJsonObject.map.map { it.key to it.value!! }.toTypedArray())
            } ?: PluginConfiguration()
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
        _managedPluginsChanged.onNext(getManagedPluginsAsJsonString())
    }

    val managedPlugins: List<Plugin> = _managedPlugins

    val availablePlugins: List<PluginDescriptor> by lazy {
        ServiceLoader.load(PluginDescriptorProvider::class.java).map { it.pluginDescriptor }
    }

    fun getManagedPluginsAsJsonString(): String {
        return json {
            array(_managedPlugins.map { plugin ->
                obj(
                    JSON_CLASS_NAME to plugin.descriptor.className,
                    JSON_CONFIGURATION to plugin.configuration?.let { conf -> obj(conf.asMap.map { it.key to it.value }) }
                )
            })
        }.toJsonString(true)
    }

    companion object {
        private const val JSON_CLASS_NAME = "className"
        private const val JSON_CONFIGURATION = "configuration"
    }
}