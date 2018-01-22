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
package org.jraf.libticker.plugin.twitter

import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import twitter4j.Paging
import twitter4j.Status
import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder
import java.util.Collections
import java.util.Comparator
import java.util.concurrent.TimeUnit

internal class TwitterClient(oAuthConsumerKey: String, oAuthConsumerSecret: String, oAuthAccessToken: String, oAuthAccessTokenSecret: String) {
    companion object {
        private var LOGGER = LoggerFactory.getLogger(TwitterClient::class.java)

        private val CHECK_PERIOD_MS = TimeUnit.MINUTES.toMillis(3)
        private const val RETRIEVE_COUNT = 30

        private val STATUS_COMPARATOR = Comparator<Status> { o1, o2 -> o1.createdAt.compareTo(o2.createdAt) }
    }

    private var checkForNewTweetsDisposable: Disposable? = null
    private val listeners = mutableListOf<StatusListener>()

    private val twitter: Twitter = TwitterFactory(
        ConfigurationBuilder()
            .setDebugEnabled(true).setOAuthConsumerKey(oAuthConsumerKey)
            .setOAuthConsumerSecret(oAuthConsumerSecret)
            .setOAuthAccessToken(oAuthAccessToken)
            .setOAuthAccessTokenSecret(oAuthAccessTokenSecret)
            .build()
    )
        .instance


    fun addListener(listener: StatusListener) = listeners.add(listener)

    fun removeListener(listener: StatusListener) = listeners.remove(listener)


    fun startClient() {
        checkForNewTweetsDisposable =
                Schedulers.computation().schedulePeriodicallyDirect(CheckForNewTweetsRunnable(), 0, CHECK_PERIOD_MS, TimeUnit.MILLISECONDS)
    }

    fun stopClient() {
        checkForNewTweetsDisposable?.dispose()
    }

    private inner class CheckForNewTweetsRunnable : Runnable {
        private var previousStatuses: List<Status>? = null

        override fun run() {
            try {
                LOGGER.debug("Checking for new tweets")
                val statusList = twitter.getUserListStatuses("bod", "news", Paging(1, RETRIEVE_COUNT))
//                val statusList = twitter.search().search(Query("eurovision")).tweets
                if (statusList.isEmpty()) {
                    LOGGER.debug("No tweets")
                    return
                }
                // Sort them
                Collections.sort(statusList, STATUS_COMPARATOR)
                val previousStatuses = statusList.toList()
                // Remove the tweets from last round (if any) to get only the new ones
                if (this.previousStatuses != null) statusList.removeAll(this.previousStatuses!!)
                this.previousStatuses = previousStatuses

                if (statusList.isEmpty()) {
                    // No change
                    LOGGER.debug("No new tweets")
                    return
                }

                // New tweets (or first time): dispatch to listeners
                LOGGER.debug("${statusList.size} new tweets")
                listeners.forEach { it.onNewStatuses(statusList) }
            } catch (e: Exception) {
                LOGGER.warn("Could not retrieve tweets", e)
            }
        }
    }
}
