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

import java.util.ArrayDeque
import java.util.ArrayList

class BasicMessageQueue(private val size: Int) : MessageQueue {
    private val messageList: ArrayList<CharSequence> = ArrayList(size * 2)
    private var messageListCurrentIndex = 0
    private val urgentMessageQueue: ArrayDeque<CharSequence> = ArrayDeque(size * 2)

    override val next: CharSequence?
        @Synchronized get() {
            // Try the urgent queue first
            if (!urgentMessageQueue.isEmpty()) return urgentMessageQueue.pop()

            // Try the normal list
            if (messageList.isEmpty()) return null
            val res = messageList[messageListCurrentIndex]
            messageListCurrentIndex = (messageListCurrentIndex + 1) % messageList.size
            return res
        }


    @Synchronized
    override fun add(vararg messages: CharSequence) {
        messageList.addAll(messages)

        // Discard old items if any
        var elementsToDiscard = 0
        if (messageList.size > size) {
            elementsToDiscard = messageList.size - size
            messageList.subList(0, elementsToDiscard).clear()
        }

        // Shift the index if elements were discarded
        messageListCurrentIndex -= elementsToDiscard
        if (messageListCurrentIndex < 0) messageListCurrentIndex = 0
    }


    @Synchronized
    override fun addUrgent(vararg messages: CharSequence) {
        urgentMessageQueue.addAll(messages)
    }
}