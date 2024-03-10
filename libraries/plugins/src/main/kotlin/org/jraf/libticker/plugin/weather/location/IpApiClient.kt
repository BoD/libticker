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

package org.jraf.libticker.plugin.weather.location

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonPrimitive
import org.jraf.libticker.plugin.util.fetch
import org.slf4j.LoggerFactory

class IpApiClient {
    companion object {
        private var LOGGER = LoggerFactory.getLogger(IpApiClient::class.java)

        private const val URL_API = "http://ip-api.com/json"
    }

    private var _currentLocation: Location? = null

    val currentLocation: Location?
        get() {
            if (_currentLocation != null) return _currentLocation
            _currentLocation = try {
                callIpApi()
            } catch (e: Throwable) {
                LOGGER.warn("Could not call api", e)
                null
            }
            return _currentLocation
        }

    private fun callIpApi(): Location {
        val rootJson: JsonObject = fetch(URL_API)
        return Location(
            rootJson["lat"]!!.jsonPrimitive.double,
            rootJson["lon"]!!.jsonPrimitive.double
        )
    }
}
