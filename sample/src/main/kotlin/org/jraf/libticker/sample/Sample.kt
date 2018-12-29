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
import org.jraf.libticker.httpconf.Configuration
import org.jraf.libticker.httpconf.HttpConf
import org.jraf.libticker.message.BasicMessageQueue
import org.jraf.libticker.plugin.api.PluginConfiguration
import org.jraf.libticker.plugin.manager.PluginManager
import org.jraf.libticker.plugin.twitter.TwitterPluginDescriptor
import java.util.concurrent.TimeUnit

fun main() {
    // Logging
//    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace")

    val messageQueue = BasicMessageQueue(40)

    val pluginManager = PluginManager(messageQueue).apply {
        // Date time
        managePlugin(
            "org.jraf.libticker.plugin.datetime.DateTimePlugin", PluginConfiguration(
                "dateLocale" to "fr"
            )
        )

        // FRC
        managePlugin("org.jraf.libticker.plugin.frc.FrcPlugin", null)

        // Weather
        managePlugin(
            "org.jraf.libticker.plugin.weather.WeatherPlugin", PluginConfiguration(
                "apiKey" to System.getenv("org.jraf.libticker.plugin.weather.WeatherPlugin.apiKey")
            )
        )

        // Btc
        managePlugin("org.jraf.libticker.plugin.btc.BtcPlugin", null)

        // Twitter
        managePlugin(
            "org.jraf.libticker.plugin.twitter.TwitterPlugin", PluginConfiguration(
                TwitterPluginDescriptor.KEY_OAUTH_CONSUMER_KEY to System.getenv("org.jraf.libticker.plugin.twitter.TwitterPlugin.oAuthConsumerKey"),
                TwitterPluginDescriptor.KEY_OAUTH_CONSUMER_SECRET to System.getenv("org.jraf.libticker.plugin.twitter.TwitterPlugin.oAuthConsumerSecret"),
                TwitterPluginDescriptor.KEY_OAUTH_ACCESS_TOKEN to System.getenv("org.jraf.libticker.plugin.twitter.TwitterPlugin.oAuthAccessToken"),
                TwitterPluginDescriptor.KEY_OAUTH_ACCESS_TOKEN_SECRET to System.getenv("org.jraf.libticker.plugin.twitter.TwitterPlugin.oAuthAccessTokenSecret"),
                TwitterPluginDescriptor.KEY_SEARCH to "list:bod/news"
            )
        )
    }

//    pluginManager.managePlugins(
//        """
//            {
//              "org.jraf.libticker.plugin.datetime.DateTimePlugin": {
//                "dateLocale": "fr"
//              },
//              "org.jraf.libticker.plugin.frc.FrcPlugin": null,
//              "org.jraf.libticker.plugin.weather.WeatherPlugin": {
//                "apiKey": "xxx"
//              },
//              "org.jraf.libticker.plugin.btc.BtcPlugin": null,
//              "org.jraf.libticker.plugin.twitter.TwitterPlugin": {
//                "oAuthConsumerKey": "xxx",
//                "oAuthConsumerSecret": "xxx",
//                "oAuthAccessToken": "xxx",
//                "oAuthAccessTokenSecret": "xxx"
//              }
//            }
//    """
//    )

    println(pluginManager.getManagedPluginsAsJsonString())

    pluginManager.managedPluginsChanged.subscribe { jsonConf ->
        println("Managed plugins changed!\n$jsonConf")
    }

    Schedulers.computation().schedulePeriodicallyDirect({
        messageQueue.next?.let(::println)
    }, 0, 5, TimeUnit.SECONDS)

    val httpConf = HttpConf(pluginManager, Configuration(port = 8043))
    httpConf.start()
    println(httpConf.getUrl())

    Object().let {
        synchronized(it) {
            it.wait()
        }
    }
}