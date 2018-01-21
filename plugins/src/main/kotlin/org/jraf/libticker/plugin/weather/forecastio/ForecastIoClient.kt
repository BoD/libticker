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
package org.jraf.android.ticker.provider.datetimeweather.weather.forecastio

import com.github.dvdme.ForecastIOLib.FIOCurrently
import com.github.dvdme.ForecastIOLib.FIODaily
import com.github.dvdme.ForecastIOLib.ForecastIO
import org.jraf.libticker.plugin.weather.WeatherCondition
import org.jraf.libticker.plugin.weather.WeatherResult
import org.jraf.libticker.plugin.weather.location.IpApiClient
import org.jraf.libticker.plugin.weather.location.Location
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit


internal class ForecastIoClient(private val apiKey: String) {
    companion object {
        private var LOGGER = LoggerFactory.getLogger(ForecastIoClient::class.java)

        private val LAST_RESULT_MAX_AGE_MS = TimeUnit.MINUTES.toMillis(10)

        private fun fixIncorrectQuotes(s: String) = if (s.startsWith("\"")) s.substring(1, s.length - 1) else s
    }

    private val ipApiClient = IpApiClient()
    private var _weather: WeatherResult? = null

    val weather: WeatherResult?
        get() {
            val lastResult = _weather
            if (lastResult != null && System.currentTimeMillis() - lastResult.timestamp <= LAST_RESULT_MAX_AGE_MS) {
                // Use the cached value
                return lastResult
            }

            val location = ipApiClient.currentLocation
            if (location == null) {
                LOGGER.warn("Could not retrieve current location")
                return lastResult
            }

            val res = callForecastIo(location)
            this._weather = res
            return res
        }

    private fun callForecastIo(location: Location): WeatherResult {
        val forecastIo = ForecastIO(apiKey)
        forecastIo.units = ForecastIO.UNITS_SI
        forecastIo.excludeURL = "hourly,minutely"

        // Make the actual API call (blocking)
        forecastIo.getForecast(location.latitude.toString(), location.longitude.toString())

        // Get current temperature
        val fioCurrently = FIOCurrently(forecastIo)
        val currentlyDataPoint = fioCurrently.get()
        val currentTemperature = currentlyDataPoint.temperature()?.toFloat() ?: 0F

        // Get today's conditions
        val fioDaily = FIODaily(forecastIo)
        val todayDataPoint = fioDaily.getDay(0)
        val todayMinTemperature = todayDataPoint.temperatureMin()?.toFloat() ?: 0F
        val todayMaxTemperature = todayDataPoint.temperatureMax()?.toFloat() ?: 0F
        val todayWeatherCondition = WeatherCondition.fromCode(fixIncorrectQuotes(todayDataPoint.icon()))

        return WeatherResult(currentTemperature, todayMinTemperature, todayMaxTemperature, todayWeatherCondition)
    }
}
