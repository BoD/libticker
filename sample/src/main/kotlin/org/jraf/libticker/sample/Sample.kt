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

package org.jraf.libticker.sample

import io.reactivex.schedulers.Schedulers
import org.jraf.libticker.message.BasicMessageQueue
import org.jraf.libticker.plugin.api.PluginConfiguration
import org.jraf.libticker.plugin.manager.PluginManager
import java.util.concurrent.TimeUnit

fun main() {
    val messageQueue = BasicMessageQueue(40)

    val pluginManager = PluginManager(messageQueue).apply {
        managePlugin(
            "org.jraf.libticker.plugin.datetime.DateTimePlugin", PluginConfiguration().apply {
                put("dateLocale", "fr")
            })
            .start()

        managePlugin("org.jraf.libticker.plugin.frc.FrcPlugin", null)
            .start()

        managePlugin("org.jraf.libticker.plugin.weather.WeatherPlugin", PluginConfiguration().apply {
            put("apiKey", System.getenv("org.jraf.libticker.plugin.weather.WeatherPlugin.apiKey"))
        })
            .start()

        managePlugin("org.jraf.libticker.plugin.btc.BtcPlugin", null).start()
        managePlugin("org.jraf.libticker.plugin.twitter.TwitterPlugin", PluginConfiguration().apply {
            put(
                "oAuthConsumerKey",
                System.getenv("org.jraf.libticker.plugin.twitter.TwitterPlugin.oAuthConsumerKey")
            )
            put(
                "oAuthConsumerSecret",
                System.getenv("org.jraf.libticker.plugin.twitter.TwitterPlugin.oAuthConsumerSecret")
            )
            put(
                "oAuthAccessToken",
                System.getenv("org.jraf.libticker.plugin.twitter.TwitterPlugin.oAuthAccessToken")
            )
            put(
                "oAuthAccessTokenSecret",
                System.getenv("org.jraf.libticker.plugin.twitter.TwitterPlugin.oAuthAccessTokenSecret")
            )
        })
            .start()
    }

    println(pluginManager.availablePlugins)

    Schedulers.computation().schedulePeriodicallyDirect({
        messageQueue.next?.let(::println)
    }, 0, 5, TimeUnit.SECONDS)

//    HttpConf.start()

    Object().let {
        synchronized(it) {
            it.wait()
        }
    }
}