/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2019-present Benoit 'BoD' Lubek (BoD@JRAF.org)
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

package org.jraf.libticker.plugin.appstorerating

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jraf.libticker.message.Message
import org.jraf.libticker.plugin.base.PeriodicPlugin
import java.util.concurrent.TimeUnit

class AppStoreRatingPlugin : PeriodicPlugin() {
    override val descriptor = AppStoreRatingPluginDescriptor.DESCRIPTOR

    override val periodMs = TimeUnit.MINUTES.toMillis(7)

    private val playStoreClient = PlayStoreClient()

    override fun queueMessage() {
        GlobalScope.launch {
            val appId = configuration!!.getString(AppStoreRatingPluginDescriptor.KEY_APP_ID)!!
            val rating = playStoreClient.retrieveRating(appId)
            messageQueue *= Message(
                text = "Current rating for $appId: $rating",
                html = formatHtmlRating(
                    rating,
                    configuration!!.getString(AppStoreRatingPluginDescriptor.KEY_TITLE)!!
                )
            )
        }
    }

    private fun formatHtmlRating(rating: Float, title: String): String {
        val ratingStr = "${rating.toInt()}<small>.${rating.toString()
            .substringAfter(".")
            .maxWidth(2)}</small>"

        val star = "<div class=\"star-container star\"></div>\n"
        val starTransparent = "<div class=\"star-container star-transparent\"></div>\n"
        val starFraction =
            "<div class=\"star-container star-transparent\"><div class=\"star-container star\" style=\"width: %1\$d%%;\"></div></div>\n"
        val stars = StringBuilder()
        for (i in 1..rating.toInt()) {
            stars.append(star)
        }
        val percent = ((rating - rating.toInt()) * 100).toInt()
        stars.append(starFraction.format(percent))
        for (i in 1 until 5 - rating.toInt()) {
            stars.append(starTransparent)
        }

        return """
<html>
<head>
  <style>
body {
  background: black;
  color: white;
  text-align: center;
  font-family: sans-serif;
}

.outer {
  display: table;
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  width: 100%;
}

.middle {
  display: table-cell;
  vertical-align: middle;
}

.inner {
  margin: 0 auto;
}

#title {
  font-size: 22px;
  font-weight: bold;
}

#score {
  width: 160px;
  height: 160px;
  margin: 0 auto;
  border-radius: 32px;
  padding: 16px;
  background-image: linear-gradient(#d5e5a3, #A4C639, #A4C639);
  font-size: 96px;
  text-align: center;
  line-height: 160px;

  box-shadow: 0px 0px 16px 0px rgba(0, 0, 0, 0.5);
  text-shadow: 0px 0px 8px rgba(0, 0, 0, 0.5);
}

.star-container {
   overflow: hidden;
   display: inline-block;
   text-align: left;
   width: 24px;
   height: 24px;
}

.star {
  background: url('data:image/png;base64,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') no-repeat left;
}

.star-transparent {
  background: url('data:image/png;base64,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') no-repeat left;
}

  </style>
</head>
<body>
  <div class="outer">
    <div class="middle">
      <div class="inner">
        <span id="title">""" + title + """</span><br><br>
          <div id="score">""" + ratingStr + """</div>
          <br>
          """ + stars + """
        </div>
      </div>
    </div>
  </body>
</html>
""".trimIndent()
    }

    private fun String.maxWidth(width: Int) = substring(0, width.coerceAtMost(length - 1))
}