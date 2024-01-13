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
import org.jraf.libticker.httpconf.HttpConf
import org.jraf.libticker.httpconf.HttpConfSettings
import org.jraf.libticker.message.BasicMessageQueue
import org.jraf.libticker.plugin.api.Configuration
import org.jraf.libticker.plugin.appstorerating.AppStoreRatingPluginDescriptor
import org.jraf.libticker.plugin.btc.BtcPluginDescriptor
import org.jraf.libticker.plugin.datetime.DateTimePluginDescriptor
import org.jraf.libticker.plugin.frc.FrcPluginDescriptor
import org.jraf.libticker.plugin.googlephotos.GooglePhotosPluginDescriptor
import org.jraf.libticker.plugin.manager.PluginManager
import org.jraf.libticker.plugin.weather.WeatherPluginDescriptor
import java.util.concurrent.TimeUnit

fun main() {
    // Logging
    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace")

    val messageQueue = BasicMessageQueue(40)

    val pluginManager = PluginManager(messageQueue).apply {
        // Date time
        managePlugin(
            "org.jraf.libticker.plugin.datetime.DateTimePlugin", Configuration(
                DateTimePluginDescriptor.KEY_DATE_LOCALE to "fr"
            )
        )

        // FRC
        managePlugin(
            "org.jraf.libticker.plugin.frc.FrcPlugin", Configuration(
                FrcPluginDescriptor.KEY_PERIOD to 5
            )
        )

        // Weather
        managePlugin(
            "org.jraf.libticker.plugin.weather.WeatherPlugin", Configuration(
                WeatherPluginDescriptor.KEY_API_KEY to System.getenv("org.jraf.libticker.plugin.weather.WeatherPlugin.apiKey"),
                WeatherPluginDescriptor.KEY_PERIOD to 5,
                WeatherPluginDescriptor.KEY_FORMATTING_LOCALE to "fr",

            )
        )

        // Btc
        managePlugin(
            "org.jraf.libticker.plugin.btc.BtcPlugin", Configuration(
                BtcPluginDescriptor.KEY_PERIOD to 5
            )
        )

        // App store rating
        managePlugin(
            "org.jraf.libticker.plugin.appstorerating.AppStoreRatingPlugin", Configuration(
                AppStoreRatingPluginDescriptor.KEY_APP_ID to "eu.qonto.qonto",
                AppStoreRatingPluginDescriptor.KEY_STORE to AppStoreRatingPluginDescriptor.KEY_STORE_ANDROID_PLAY_STORE,
                AppStoreRatingPluginDescriptor.KEY_TITLE to "HelloMundo (Android)",
                AppStoreRatingPluginDescriptor.KEY_PERIOD to 5
            )
        )

        // Google photos
        managePlugin(
            "org.jraf.libticker.plugin.googlephotos.GooglePhotosPlugin", Configuration(
                GooglePhotosPluginDescriptor.KEY_PERIOD to 1,
                GooglePhotosPluginDescriptor.KEY_CLIENT_ID to "200586986744-9c4qkqc87je1mc2h1474dlvm9k3pqpc9.apps.googleusercontent.com",
                GooglePhotosPluginDescriptor.KEY_CLIENT_SECRET to "xxx",
                GooglePhotosPluginDescriptor.KEY_REFRESH_TOKEN to "xxx"
            )
        )

        // Global conf
        globalConfiguration.put("displayWidth", 1024)
        globalConfiguration.put("displayHeight", 768)
    }

//    val pluginManager = PluginManager(messageQueue).apply {
//        managePlugins(
//            """
//[{
//  "className": "org.jraf.libticker.plugin.datetime.DateTimePlugin",
//  "configuration": {
//    "dateLocale": "fr"
//  }
//}, {
//  "className": "org.jraf.libticker.plugin.googlephotos.FrcPlugin",
//  "configuration": null
//}, {
//  "className": "org.jraf.libticker.plugin.weather.WeatherPlugin",
//  "configuration": {
//    "apiKey": "xxx"
//  }
//}, {
//  "className": "org.jraf.libticker.plugin.btc.BtcPlugin",
//  "configuration": null
//}, {
//  "className": "org.jraf.libticker.plugin.twitter.TwitterPlugin",
//  "configuration": {
//    "oAuthConsumerKey": "xxx",
//    "oAuthConsumerSecret": "xxx",
//    "oAuthAccessToken": "xxx",
//    "oAuthAccessTokenSecret": "xxx",
//    "search": "list:bod/news"
//  }
//}, {
//  "className": "org.jraf.libticker.plugin.appstorerating.AppStoreRatingPlugin",
//  "configuration": {
//    "appId": "org.jraf.android.latoureiffel",
//    "store": "Android Play Store",
//    "title": "HelloMundo (Android)"
//  }
//}, {
//  "className": "org.jraf.libticker.plugin.appstorerating.AppStoreRatingPlugin",
//  "configuration": {
//    "appId": "org.jraf.android.bikey",
//    "store": "Android Play Store",
//    "title": "Bikey (Android)"
//  }
//}]
//"""
//        )
//    }

    println(pluginManager.getManagedPluginsAsJsonString())

    pluginManager.managedPluginsChanged.subscribe { jsonConf ->
        println("Managed plugins changed!\n$jsonConf")
    }

    Schedulers.computation().schedulePeriodicallyDirect({
        messageQueue.getNext()?.let(::println)
    }, 0, 5, TimeUnit.SECONDS)

    val httpConf = HttpConf(pluginManager, HttpConfSettings(port = 8043))
    httpConf.start()
    println(httpConf.getUrl())

    Object().let {
        synchronized(it) {
            it.wait()
        }
    }
}
