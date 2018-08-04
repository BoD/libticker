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
package org.jraf.libticker.plugin.btc

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import org.jraf.libticker.plugin.PeriodicPlugin
import org.slf4j.LoggerFactory
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit

class BtcPlugin : PeriodicPlugin() {
    companion object {
        private var LOGGER = LoggerFactory.getLogger(BtcPlugin::class.java)

        private const val URL_API = "https://blockchain.info/ticker"
    }

    override val periodMs = TimeUnit.MINUTES.toMillis(6)

    override fun queueMessage() {
        try {
            val connection = URL(URL_API).openConnection() as HttpURLConnection
            val value = try {
                val jsonStr = connection.inputStream.bufferedReader().readText()
                val rootJson: JsonObject = Parser().parse(StringBuilder(jsonStr)) as JsonObject
                rootJson.obj("EUR")!!.float("15m")!!
            } finally {
                connection.disconnect()
            }

            messageQueue *= resourceBundle.getString("btc_value").format(value.toInt())
        } catch (e: Exception) {
            LOGGER.warn("Could not get btc value", e)
        }
    }
}