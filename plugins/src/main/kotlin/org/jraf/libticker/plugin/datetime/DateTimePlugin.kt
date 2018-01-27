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

package org.jraf.libticker.plugin.datetime

import org.jraf.libticker.message.MessageQueue
import org.jraf.libticker.plugin.PeriodicPlugin
import org.jraf.libticker.plugin.api.PluginConfiguration
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class DateTimePlugin : PeriodicPlugin() {
    companion object {
        private val TIME_FORMAT = SimpleDateFormat("HH:mm")
    }

    override val periodMs = TimeUnit.MINUTES.toMillis(5)
    override val initialDelayMs get() = periodMs - (System.currentTimeMillis() % periodMs)

    private lateinit var dateLocale: Locale
    private val dateFormat by lazy { DateFormat.getDateInstance(DateFormat.FULL, dateLocale) }


    override fun init(messageQueue: MessageQueue, configuration: PluginConfiguration?) {
        super.init(messageQueue, configuration)
        dateLocale = configuration?.optString("dateLocale", null).let {
            if (it == null) Locale.getDefault() else Locale.forLanguageTag(it)
        }
    }

    override fun queueMessage() {
        val date = Date()
        messageQueue.addUrgent(dateFormat.format(date).capitalize(), TIME_FORMAT.format(date))
    }
}