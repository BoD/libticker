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

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

class IosAppStoreClient {
    suspend fun retrieveRating(appId: String): Float {
        var text = withContext(Dispatchers.IO) { URL(URL_APP_PAGE.format(appId)).readText() }
        text = text.substringAfter(DELIM_0).substringBefore(DELIM_1)
        return text.toFloat()
    }

    companion object {
        private const val URL_APP_PAGE = "https://itunes.apple.com/fr/app/%1\$s"
        private const val DELIM_0 = "\"ratingValue\":"
        private const val DELIM_1 = ","
    }
}