/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2024-present Benoit 'BoD' Lubek (BoD@JRAF.org)
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

package org.jraf.libticker.plugin.weather.weatherkit

import org.jraf.libticker.plugin.util.fetch
import org.jraf.libticker.plugin.weather.WeatherCondition
import org.jraf.libticker.plugin.weather.WeatherResult
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class WeatherKitClient(
    private val jwtToken: String
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WeatherKitClient::class.java)
    }

    fun fetchWeather(
        latitude: Double,
        longitude: Double,
    ): WeatherResult? {
        val localTodayAtMidnightInUtc = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).atOffset(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_DATE_TIME)
        val localTodayAt235959InUtc = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).atOffset(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_DATE_TIME)
        val jsonWeatherResult: JsonWeatherResult = try {
            val url =
                "https://weatherkit.apple.com/api/v1/weather/fr/$latitude/$longitude?dataSets=currentWeather,forecastDaily&dailyStart=$localTodayAtMidnightInUtc&dailyEnd=$localTodayAt235959InUtc"
            LOGGER.debug("Fetching weather from $url")
            fetch(url, "Authorization" to "Bearer $jwtToken")
        } catch (e: Exception) {
            LOGGER.warn("Could not fetch weather", e)
            return null
        }
        LOGGER.debug("jsonWeatherResult: {}", jsonWeatherResult)
        return WeatherResult(
            currentTemperature = jsonWeatherResult.currentWeather.temperature,
            todayMinTemperature = jsonWeatherResult.forecastDaily.days[0].temperatureMin,
            todayMaxTemperature = jsonWeatherResult.forecastDaily.days[0].temperatureMax,
            todayWeatherCondition = when (jsonWeatherResult.currentWeather.conditionCode) {
                // Full list at https://gist.github.com/mikesprague/048a93b832e2862050356ca233ef4dc1
                "Clear" -> if (jsonWeatherResult.currentWeather.daylight) WeatherCondition.CLEAR_DAY else WeatherCondition.CLEAR_NIGHT
                "Rain" -> WeatherCondition.RAIN
                "Snow" -> WeatherCondition.SNOW
                "Sleet" -> WeatherCondition.SLEET
                "Windy" -> WeatherCondition.WIND
                "Foggy" -> WeatherCondition.FOG
                "Cloudy" -> WeatherCondition.CLOUDY
                "PartlyCloudy" -> if (jsonWeatherResult.currentWeather.daylight) WeatherCondition.PARTLY_CLOUDY_DAY else WeatherCondition.PARTLY_CLOUDY_NIGHT
                "MostlyCloudy" -> if (jsonWeatherResult.currentWeather.daylight) WeatherCondition.MOSTLY_CLOUDY_DAY else WeatherCondition.MOSTLY_CLOUDY_NIGHT
                else -> WeatherCondition.UNKNOWN
            }
        )
    }
}
