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
import kotlinx.html.i
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.link
import kotlinx.html.option
import kotlinx.html.script
import kotlinx.html.select
import kotlinx.html.span
import kotlinx.html.stream.appendHTML
import kotlinx.html.style
import kotlinx.html.table
import kotlinx.html.td
import kotlinx.html.title
import kotlinx.html.tr
import kotlinx.html.unsafe
import org.jraf.libticker.plugin.api.PluginConfigurationItemType
import org.jraf.libticker.plugin.manager.PluginManager

fun indexHtml(
    pluginManager: PluginManager,
    httpConfSettings: HttpConfSettings
): String {
    return StringBuilder().apply {
        appendHTML().apply {
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
                title("${httpConfSettings.appName} ${httpConfSettings.appVersion} configuration")
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
    width: 720px;
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

.empty {
    margin-left: 16px;
}

                                """.trimIndent()
                        )
                    }
                }
            }

            body {
                h3 { +"${httpConfSettings.appName} ${httpConfSettings.appVersion} configuration" }
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
                                                        +plugin.pluginConfiguration[confItem.key].toString()
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
                                    // More info
                                    configurationDescriptor.moreInfo?.let { moreInfo ->
                                        table(classes = "no-border") {
                                            tr {
                                                td(classes = "no-border") {
                                                    i(classes = "material-icons md-dark md-24") { +"info" }
                                                }
                                                td(classes = "no-border") {
                                                    unsafe { raw(moreInfo) }
                                                }
                                            }
                                        }
                                    }
                                    div {
                                        style = "overflow-x: auto; white-space: nowrap;"

                                        for ((confItemIdx, confItem) in configurationDescriptor.itemDescriptors.withIndex()) {
                                            div(classes = "mdl-textfield mdl-js-textfield mdl-textfield--floating-label") {
                                                if (confItem.type is PluginConfigurationItemType.ChoiceType) {
                                                    select(classes = "mdl-textfield__input") {
                                                        name = "conf_${confItem.key}"
                                                        id = "conf_${confItem.key}"
                                                        style = "-webkit-appearance: none; -moz-appearance : none;"

                                                        for (choice in (confItem.type as PluginConfigurationItemType.ChoiceType).choices) {
                                                            option {
                                                                value = choice
                                                                +choice
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    input(
                                                        classes = "mdl-textfield__input",
                                                        type = InputType.text,
                                                        name = "conf_${confItem.key}"
                                                    ) {
                                                        id = "conf_${confItem.key}"
                                                        value = confItem.defaultValue ?: ""
                                                        if (confItem.type is PluginConfigurationItemType.NumberType) {
                                                            pattern = "-?[0-9]*(\\.[0-9]+)?"
                                                        }
                                                    }
                                                }
                                                label(classes = "mdl-textfield__label") {
                                                    htmlFor = "conf_${confItem.key}"
                                                    +(confItem.displayName + if (!confItem.required) " (optional)" else "")
                                                }
                                                if (confItem.type is PluginConfigurationItemType.NumberType) {
                                                    span(classes = "mdl-textfield__error") {
                                                        +"Must be a number"
                                                    }
                                                }
                                            }
                                            confItem.moreInfo?.let { moreInfo ->
                                                unsafe {
                                                    raw(moreInfo)
                                                }
                                            }

                                            if (confItemIdx != configurationDescriptor.itemDescriptors.lastIndex) br()
                                        }
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
    }.toString()
}