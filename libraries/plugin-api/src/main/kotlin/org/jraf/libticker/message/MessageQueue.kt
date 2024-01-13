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

package org.jraf.libticker.message

import org.jraf.libticker.plugin.api.Plugin

interface MessageQueue {
    fun add(vararg messages: Message)
    fun addUrgent(vararg messages: Message)
    fun set(plugin: Plugin, vararg message: Message)
    fun unset(plugin: Plugin)

    fun getNext(): Message?

    operator fun plusAssign(message: Message) = add(message)
    operator fun timesAssign(message: Message) = addUrgent(message)
    operator fun set(plugin: Plugin, message: Message) = set(plugin, *arrayOf(message))
}