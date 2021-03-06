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

package org.jraf.libticker.message

data class Message(
    /**
     * Plain text of this message.
     */
    val text: String,

    /**
     * A formatted version of the text (may contain simple HTML markup).
     */
    val textFormatted: String = text,

    /**
     * An HTML version of this message (may be null), to be displayed on a browser/web view.
     */
    val html: String? = null,

    /**
     * Optional URI to the source of this message.
     */
    val uri: String? = null,

    /**
     * Optional URI to an image.
     */
    val imageUri: String? = null,

    /**
     * Hints that can be optionally used to display this message.
     */
    val hints: Map<String, String> = mapOf()
)