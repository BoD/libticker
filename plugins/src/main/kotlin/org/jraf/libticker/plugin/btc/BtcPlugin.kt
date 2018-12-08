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
import org.jraf.libticker.message.Message
import org.jraf.libticker.plugin.PeriodicPlugin
import org.slf4j.LoggerFactory
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class BtcPlugin : PeriodicPlugin() {
    companion object {
        private var LOGGER = LoggerFactory.getLogger(BtcPlugin::class.java)

        private const val URL_CURRENT_VALUE = "https://blockchain.info/ticker"
        private const val URL_PAST_VALUES = "https://api.blockchain.info/charts/market-price?timespan=30days"
        private const val URL_CHART =
            "https://chart.googleapis.com/chart?cht=ls&chs=320x200&chf=bg,s,000000&chls=6.0&chd=t:%1\$s"
    }

    override val periodMs = TimeUnit.MINUTES.toMillis(6)

    override fun queueMessage() {
        try {
            val currentValueJson = fetch(URL_CURRENT_VALUE)
            val currentValue = currentValueJson.obj("EUR")!!.float("15m")!!.toInt()

            val pastValuesJson = fetch(URL_PAST_VALUES)
            val pastValues = pastValuesJson.array<JsonObject>("values")!!
                .map { it.float("y")!! }
                .normalize()
                .map { it.roundToInt() }

            messageQueue *= Message(
                text = resourceBundle.getString("btc_value_plain").format(currentValue),
                textFormatted = resourceBundle.getString("btc_value_formatted").format(currentValue),
                imageUri = URL_CHART.format(pastValues.joinToString(","))
            )
        } catch (t: Throwable) {
            LOGGER.warn("Could not call api", t)
        }
    }

    private fun Iterable<Float>.normalize(): Iterable<Float> {
        val min = min()!!
        val max = max()!!
        return map { (100F / (max - min)) * (it - min) }
    }

    private fun fetch(url: String): JsonObject {
        val connection = URL(url).openConnection() as HttpURLConnection
        return try {
            val jsonStr = connection.inputStream.bufferedReader().readText()
            Parser.default().parse(StringBuilder(jsonStr)) as JsonObject
        } finally {
            connection.disconnect()
        }
    }
}