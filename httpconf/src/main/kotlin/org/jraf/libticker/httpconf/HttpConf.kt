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

import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.html.respondHtml
import io.ktor.request.receiveParameters
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.toMap
import kotlinx.html.FormEncType
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.body
import kotlinx.html.br
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h3
import kotlinx.html.h4
import kotlinx.html.h5
import kotlinx.html.head
import kotlinx.html.hr
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.link
import kotlinx.html.script
import kotlinx.html.span
import kotlinx.html.style
import kotlinx.html.table
import kotlinx.html.td
import kotlinx.html.title
import kotlinx.html.tr
import kotlinx.html.unsafe
import org.jraf.libticker.plugin.api.PluginConfiguration
import org.jraf.libticker.plugin.api.PluginConfigurationItemType
import org.jraf.libticker.plugin.manager.PluginManager


class HttpConf(
    private val pluginManager: PluginManager,
    private val configuration: Configuration = Configuration()
) {
    fun start() {
        embeddedServer(Netty, configuration.port) {
            //            install(DefaultHeaders)
            install(CallLogging)

            routing {
                route("/") {
                    get {
                        call.respondHtml {
                            head {
                                link(
                                    rel = "stylesheet",
                                    href = "https://fonts.googleapis.com/icon?family=Material+Icons"
                                )
                                link(
                                    rel = "stylesheet",
                                    href = "https://code.getmdl.io/1.3.0/material.indigo-pink.min.css"
                                )
                                script(src = "https://code.getmdl.io/1.3.0/material.min.js") {
                                    defer = true
                                }
                                title("${configuration.appName} ${configuration.appVersion} configuration")
                                style {
                                    unsafe {
                                        raw(
                                            """

body {
    padding: 16px;
    font-family: 'Roboto', 'Helvetica', 'Arial', sans-serif !important;
    background-color: #f1f1f1;
    width: auto;
}

table {
    margin: 4px 0 0 0;
    border-collapse: collapse;
    color: rgba(0,0,0,.54);
    width: 100%;
    font-size: 14px;
}

table, td {
    border: 1px solid rgba(0,0,0,.54);
}

td:not(:last-child) {
    white-space: nowrap;
}

td:last-child {
    width: 100%;
}

td {
    padding: 8px
}

.no-border {
    border: 0px none;
    background: #fffde7
}

td:not(:last-child).no-border {
    padding-right: 0;
}

form {
    margin: 0 0 0 0;
}

.card.mdl-card {
    width: 640px;
    min-height: 0;
}

.card > .mdl-card__title > h5 {
  margin: 0;
}

.card > .mdl-card__title > h5 > .mdl-card__subtitle-text {
    margin-top: 4px;
    color: rgba(0,0,0,.33)
}

h3 {
    margin: 0;
    margin-left: 16px;
}

h4 {
    margin-left: 16px;
}

subtitle1 {
    color:
}

.empty {
    margin-left: 16px;
}

                                """.trimIndent()
                                        )
                                    }
                                }
                            }

                            body {
                                h3 { +"${configuration.appName} ${configuration.appVersion} configuration" }
                                h4 { +"Running plugins" }
                                if (pluginManager.managedPlugins.isEmpty()) {
                                    span(classes = "empty") {
                                        +"(No running plugins)"
                                    }
                                } else {
                                    for ((idx, plugin) in pluginManager.managedPlugins.withIndex()) {
                                        div(classes = "card mdl-card mdl-shadow--2dp") {
                                            // Title
                                            div(classes = "mdl-card__title mdl-card--border") {
                                                h5 {
                                                    div(classes = "mdl-card__title-text") { +plugin.descriptor.displayName }
                                                    div(classes = "mdl-card__subtitle-text") { +plugin.descriptor.className }
                                                }
                                            }

                                            // Configuration
                                            div(classes = "mdl-card__supporting-text") {
                                                val configurationDescriptor = plugin.descriptor.configurationDescriptor
                                                if (configurationDescriptor == null) {
                                                    +"(Not configurable)"
                                                } else {
                                                    +"Configuration:"
                                                    div {
                                                        style = "overflow-x: auto;"
                                                        table {
                                                            for (confItem in configurationDescriptor.itemDescriptors) {
                                                                tr {
                                                                    td {
                                                                        +confItem.displayName
                                                                    }
                                                                    td {
                                                                        +plugin.configuration!![confItem.key].toString()
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            form(
                                                action = "/action",
                                                method = FormMethod.post,
                                                encType = FormEncType.applicationXWwwFormUrlEncoded
                                            ) {
                                                input(type = InputType.hidden, name = "action") {
                                                    value = "unmanage"
                                                }
                                                input(type = InputType.hidden, name = "idx") {
                                                    value = "$idx"
                                                }

                                                // Actions
                                                div(classes = "mdl-card__actions mdl-card--border") {
                                                    input(
                                                        type = InputType.submit,
                                                        classes = "mdl-button mdl-js-button mdl-js-ripple-effect mdl-button--accent"
                                                    ) {
                                                        value = "Remove"
                                                    }
                                                }
                                            }
                                        }
                                        if (idx != pluginManager.managedPlugins.lastIndex) br()
                                    }
                                }
                                br()
                                hr()
                                h4 { +"Available plugins" }
                                for ((idx, descriptor) in pluginManager.availablePlugins.withIndex()) {
                                    div(classes = "card mdl-card mdl-shadow--2dp") {
                                        // Title
                                        div(classes = "mdl-card__title mdl-card--border") {
                                            h5 {
                                                div(classes = "mdl-card__title-text") { +descriptor.displayName }
                                                div(classes = "mdl-card__subtitle-text") { +descriptor.className }
                                            }
                                        }

                                        // Configuration
                                        form(
                                            action = "/action",
                                            method = FormMethod.post,
                                            encType = FormEncType.applicationXWwwFormUrlEncoded
                                        ) {
                                            div(classes = "mdl-card__supporting-text") {
                                                input(type = InputType.hidden, name = "action") {
                                                    value = "manage"
                                                }
                                                input(type = InputType.hidden, name = "className") {
                                                    value = descriptor.className
                                                }
                                                val configurationDescriptor = descriptor.configurationDescriptor
                                                if (configurationDescriptor == null) {
                                                    +"(Not configurable)"
                                                } else {
                                                    for (confItem in configurationDescriptor.itemDescriptors) {
                                                        div(classes = "mdl-textfield mdl-js-textfield mdl-textfield--floating-label") {
                                                            input(
                                                                classes = "mdl-textfield__input",
                                                                type = InputType.text,
                                                                name = "conf_${confItem.key}"
                                                            ) {
                                                                id = "conf_${confItem.key}"
                                                                value = confItem.defaultValue ?: ""
                                                                if (confItem.type == PluginConfigurationItemType.NUMBER) {
                                                                    pattern = "-?[0-9]*(\\.[0-9]+)?"
                                                                }
                                                            }
                                                            label(classes = "mdl-textfield__label") {
                                                                htmlFor = "conf_${confItem.key}"
                                                                +(confItem.displayName + if (!confItem.required) " (optional)" else "")
                                                            }
                                                            if (confItem.type == PluginConfigurationItemType.NUMBER) {
                                                                span(classes = "mdl-textfield__error") {
                                                                    +"Must be a number"
                                                                }
                                                            }
                                                        }
                                                        confItem.moreInfo?.let { moreInfo -> +moreInfo }
                                                    }
                                                }
                                            }

                                            // Actions
                                            div(classes = "mdl-card__actions mdl-card--border") {
                                                input(
                                                    type = InputType.submit,
                                                    classes = "mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect"
                                                ) {
                                                    value = "Add"
                                                }
                                            }
                                        }
                                    }
                                    if (idx != pluginManager.availablePlugins.lastIndex) br()
                                }
                            }
                        }
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

    fun getUrl(): String {
        return "http://${getLocalHostLanAddress()!!.hostAddress}:${configuration.port}/"
    }
}