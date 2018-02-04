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

import org.jraf.android.ticker.provider.datetimeweather.weather.forecastio.ForecastIoClient
import org.jraf.libticker.message.MessageQueue
import org.jraf.libticker.plugin.PeriodicPlugin
import org.jraf.libticker.plugin.api.PluginConfiguration
import java.util.Locale
import java.util.concurrent.TimeUnit

class WeatherPlugin : PeriodicPlugin() {
    override val periodMs = TimeUnit.MINUTES.toMillis(7)

    private lateinit var forecastIoClient: ForecastIoClient
    private lateinit var formattingLocale: Locale

    override fun init(messageQueue: MessageQueue, configuration: PluginConfiguration?) {
        super.init(messageQueue, configuration)
        formattingLocale = configuration?.optString("formattingLocale", null).let {
            if (it == null) Locale.getDefault() else Locale.forLanguageTag(it)
        }
        forecastIoClient = ForecastIoClient(configuration!!.getString("apiKey"))
    }

    override fun queueMessage() {
        forecastIoClient.weather?.let { weatherResult ->
            val weatherNow = resourceBundle.getString("weather_now").format(
                formattingLocale,
                weatherResult.todayWeatherCondition.symbol,
                weatherResult.currentTemperature
            )
            val weatherMin =
                resourceBundle.getString("weather_min").format(formattingLocale, weatherResult.todayMinTemperature)
            val weatherMax =
                resourceBundle.getString("weather_max").format(formattingLocale, weatherResult.todayMaxTemperature)

            messageQueue.addUrgent(weatherNow, weatherMin, weatherMax)
        }
    }
}