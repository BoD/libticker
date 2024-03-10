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

package org.jraf.libticker.plugin.weather

import org.jraf.libticker.message.Message
import org.jraf.libticker.message.MessageQueue
import org.jraf.libticker.plugin.api.Configuration
import org.jraf.libticker.plugin.base.PeriodicPlugin
import org.jraf.libticker.plugin.weather.WeatherPluginDescriptor.KEY_FORMATTING_LOCALE
import org.jraf.libticker.plugin.weather.WeatherPluginDescriptor.KEY_JWT_TOKEN
import org.jraf.libticker.plugin.weather.WeatherPluginDescriptor.KEY_PERIOD
import org.jraf.libticker.plugin.weather.location.IpApiClient
import org.jraf.libticker.plugin.weather.weatherkit.WeatherKitClient
import org.slf4j.LoggerFactory
import java.util.Locale
import java.util.concurrent.TimeUnit

class WeatherPlugin : PeriodicPlugin() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WeatherPlugin::class.java)
    }

    override val descriptor = WeatherPluginDescriptor.DESCRIPTOR

    override val periodMs get() = TimeUnit.MINUTES.toMillis(pluginConfiguration.getNumber(KEY_PERIOD).toLong())

    private lateinit var weatherKitClient: WeatherKitClient
    private lateinit var formattingLocale: Locale
    private lateinit var ipApiClient: IpApiClient

    override fun init(
        messageQueue: MessageQueue,
        pluginConfiguration: Configuration,
        globalConfiguration: Configuration
    ) {
        super.init(messageQueue, pluginConfiguration, globalConfiguration)
        formattingLocale = pluginConfiguration.getStringOrNull(KEY_FORMATTING_LOCALE).let {
            if (it == null) Locale.getDefault() else Locale.forLanguageTag(it)
        }
        weatherKitClient = WeatherKitClient(pluginConfiguration.getString(KEY_JWT_TOKEN))
        ipApiClient = IpApiClient()
    }

    override fun queueMessage() {
        val location = ipApiClient.currentLocation
        if (location == null) {
            LOGGER.warn("Could not get location")
            return
        }
        weatherKitClient.fetchWeather(latitude = location.latitude, longitude = location.longitude)
            ?.let { weatherResult ->
                // Plain
                val weatherNowPlain = resourceBundle.getString("weather_now_plain").format(
                    formattingLocale,
                    weatherResult.todayWeatherCondition.symbol,
                    weatherResult.currentTemperature
                )
                val weatherMinPlain =
                    resourceBundle.getString("weather_min_plain")
                        .format(formattingLocale, weatherResult.todayMinTemperature)
                val weatherMaxPlain =
                    resourceBundle.getString("weather_max_plain")
                        .format(formattingLocale, weatherResult.todayMaxTemperature)

                // Formatted
                val weatherNowFormatted = resourceBundle.getString("weather_now_formatted").format(
                    formattingLocale,
                    weatherResult.todayWeatherCondition.symbol,
                    weatherResult.currentTemperature
                )
                val weatherMinFormatted =
                    resourceBundle.getString("weather_min_formatted")
                        .format(formattingLocale, weatherResult.todayMinTemperature)
                val weatherMaxFormatted =
                    resourceBundle.getString("weather_max_formatted")
                        .format(formattingLocale, weatherResult.todayMaxTemperature)

                messageQueue.set(
                    this,
                    Message(text = weatherNowPlain, textFormatted = weatherNowFormatted),
                    Message(text = weatherMinPlain, textFormatted = weatherMinFormatted),
                    Message(text = weatherMaxPlain, textFormatted = weatherMaxFormatted)
                )
            }
    }
}
