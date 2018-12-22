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
        configuration: PluginConfiguration?,
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
        val managePluginsJsonObject = parser.parse(StringBuilder(jsonString)) as JsonObject
        for (pluginClassName in managePluginsJsonObject.keys) {
            val configuration = managePluginsJsonObject.obj(pluginClassName)?.let { configurationJsonObject ->
                PluginConfiguration(*configurationJsonObject.map.map { it.key to it.value!! }.toTypedArray())
            }
            managePlugin(pluginClassName, configuration)
        }
        if (notifyListeners) notifyListeners()
    }

    fun unmanagePlugin(plugin: Plugin, notifyListeners: Boolean = false) {
        if (plugin.isRunning) plugin.stop()
        _managedPlugins -= plugin
        if (notifyListeners) notifyListeners()
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
            obj(_managedPlugins.map { plugin ->
                plugin.descriptor.className to plugin.configuration?.let { conf -> obj(conf.asMap.map { it.key to it.value }) }
            })
        }.toJsonString(true)
    }
}