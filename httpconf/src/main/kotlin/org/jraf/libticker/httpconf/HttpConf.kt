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

package org.jraf.libticker.httpconf

import fi.iki.elonen.NanoHTTPD
import org.jraf.libticker.plugin.api.PluginConfiguration
import org.jraf.libticker.plugin.api.PluginConfigurationItemType
import org.jraf.libticker.plugin.manager.PluginManager


class HttpConf(
    private val pluginManager: PluginManager,
    private val configuration: Configuration = Configuration()
) {
    private val server by lazy {
        object : NanoHTTPD(configuration.port) {
            override fun serve(session: IHTTPSession): NanoHTTPD.Response {
                return when (session.uri) {
                    "/" -> newFixedLengthResponse(indexHtml(pluginManager, configuration))

                    "/action" -> {
                        session.parseBody(mutableMapOf<String, String>())
                        val parameters = session.parameters.mapValues { it.value.first() }
                        when (parameters["action"]) {
                            "unmanage" -> {
                                val idx = parameters["idx"]
                                if (idx == null || idx.toIntOrNull() == null) {
                                    badRequest()
                                } else {
                                    pluginManager.unmanagePlugin(pluginManager.managedPlugins[idx.toInt()], true)
                                    redirect("/")
                                }
                            }

                            "manage" -> {
                                val className = parameters["className"]
                                if (className == null) {
                                    badRequest()
                                } else {
                                    val confItemsAsStrings = parameters
                                        .filterKeys { key -> key.startsWith("conf_") }
                                        .mapKeys { entry -> entry.key.substringAfter("conf_") }
                                    val configuration = mapToPluginConfiguration(className, confItemsAsStrings)
                                    pluginManager.managePlugin(className, configuration, true)
                                    redirect("/")
                                }
                            }

                            else -> badRequest()
                        }
                    }

                    else -> badRequest()
                }
            }
        }
    }

    private fun badRequest() = NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, null, null)

    private fun redirect(location: String) = NanoHTTPD.newFixedLengthResponse(
        NanoHTTPD.Response.Status.REDIRECT,
        null,
        null
    ).apply {
        addHeader("Location", location)
    }

    private fun mapToPluginConfiguration(className: String, map: Map<String, String>): PluginConfiguration? {
        val pluginDescriptor = pluginManager.availablePlugins.first { it.className == className }
        val configurationDescriptor = pluginDescriptor.configurationDescriptor ?: return null
        val res = PluginConfiguration()
        map.forEach { (key, value) ->
            val itemDescriptor = configurationDescriptor.itemDescriptors.first { it.key == key }
            when (itemDescriptor.type) {
                is PluginConfigurationItemType.StringType, is PluginConfigurationItemType.ChoiceType -> res.put(
                    key,
                    value
                )
                is PluginConfigurationItemType.NumberType -> res.put(key, value.toDouble())
                is PluginConfigurationItemType.BooleanType -> res.put(key, value.toBoolean())
            }
        }
        return res
    }

    fun start() {
        server.start()
    }

    fun stop() {
        server.stop()
    }

    fun getUrl(): String {
        return "http://${getLocalHostLanAddress()!!.hostAddress}${if (configuration.port == 80) "" else ":${configuration.port}"}/"
    }
}