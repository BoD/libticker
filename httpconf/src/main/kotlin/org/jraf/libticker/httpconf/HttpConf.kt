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

import freemarker.cache.ClassTemplateLoader
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.freemarker.FreeMarker
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.toMap
import org.jraf.libticker.plugin.api.PluginConfiguration
import org.jraf.libticker.plugin.api.PluginConfigurationItemType
import org.jraf.libticker.plugin.manager.PluginManager

class HttpConf(
    private val pluginManager: PluginManager,
    private val configuration: Configuration = Configuration()
) {
    fun start() {
        embeddedServer(Netty, configuration.port) {
            install(DefaultHeaders)
            install(CallLogging)
            install(FreeMarker) {
                templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
            }

            routing {
                route("/") {
                    get {
                        call.respond(
                            FreeMarkerContent(
                                "index.ftl",
                                mapOf(
                                    "configuration" to configuration,
                                    "managedPlugins" to pluginManager.managedPlugins,
                                    "availablePlugins" to pluginManager.availablePlugins
                                )
                            )
                        )
                    }
                }

                route("/action") {
                    post {
                        val parameters = call.receiveParameters()
                        when (parameters["action"]) {
                            "unmanage" -> {
                                val idx = parameters["idx"]
                                if (idx == null || idx.toIntOrNull() == null) {
                                    call.respondText("Error")
                                } else {
                                    pluginManager.unmanagePlugin(pluginManager.managedPlugins[idx.toInt()], true)
                                    call.respondRedirect("/", permanent = false)
                                }
                            }

                            "manage" -> {
                                val className = parameters["className"]
                                if (className == null) {
                                    call.respondText("Error")
                                } else {
                                    val confItemsAsStrings = parameters.toMap()
                                        .filterKeys { key -> key.startsWith("conf_") }
                                        .mapKeys { entry -> entry.key.substringAfter("conf_") }
                                        .mapValues { entry -> entry.value.first() }
                                    val configuration = mapToPluginConfiguration(className, confItemsAsStrings)
                                    pluginManager.managePlugin(className, configuration, true)
                                    call.respondRedirect("/", permanent = false)
                                }
                            }

                            else -> call.respondText("Error")
                        }
                    }
                }
            }
        }.start(wait = false)
    }

    private fun mapToPluginConfiguration(className: String, map: Map<String, String>): PluginConfiguration? {
        val pluginDescriptor = pluginManager.availablePlugins.first { it.className == className }
        val configurationDescriptor = pluginDescriptor.configurationDescriptor ?: return null
        val res = PluginConfiguration()
        map.forEach { (key, value) ->
            val itemDescriptor = configurationDescriptor.itemDescriptors.first { it.key == key }
            when (itemDescriptor.type) {
                PluginConfigurationItemType.STRING -> res.put(key, value)
                PluginConfigurationItemType.NUMBER -> res.put(key, value.toDouble())
                PluginConfigurationItemType.BOOLEAN -> res.put(key, value.toBoolean())
            }
        }
        return res
    }
}