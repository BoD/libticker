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

import org.jraf.libticker.message.MessageQueue
import org.jraf.libticker.plugin.api.Plugin
import org.jraf.libticker.plugin.api.PluginConfiguration

class PluginManager(private val messageQueue: MessageQueue) {
    private val plugins = mutableListOf<Plugin>()

    fun addPlugin(pluginClassName: String, configuration: PluginConfiguration?): PluginManager {
        (Class.forName(pluginClassName).newInstance() as Plugin).let {
            it.init(messageQueue, configuration)
            plugins += it
        }
        return this
    }

    fun addPlugins(vararg pluginClassAndConf: Pair<String, PluginConfiguration?>): PluginManager {
        pluginClassAndConf.forEach { addPlugin(it.first, it.second) }
        return this
    }

    fun start() = plugins.forEach { it.start() }

    fun stop() = plugins.forEach { it.stop() }
}