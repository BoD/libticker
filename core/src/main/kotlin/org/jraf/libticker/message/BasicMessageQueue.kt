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

import org.jraf.libticker.plugin.api.Plugin
import java.util.ArrayDeque
import java.util.ArrayList

class BasicMessageQueue(private val size: Int = 100, private val messagesFromMessageList: Int = 4) : MessageQueue {
    private val messageList: ArrayList<Message> = ArrayList(size * 2)
    private var messageListIndex = 0
    private val urgentMessageQueue: ArrayDeque<Message> = ArrayDeque(size * 2)
    private val messagesByPlugin = LinkedHashMap<Plugin, List<Message>>()
    private var messagesByPluginPluginIndex = 0
    private var messagesByPluginMessageIndex = 0
    private var globalIndex = 0

    @Synchronized
    override fun getNext(): Message? {
        // Try the urgent queue first
        if (!urgentMessageQueue.isEmpty()) return urgentMessageQueue.pop()

        // Use the plugin list
        if (globalIndex % (messagesFromMessageList + 1) == 0 || messageList.isEmpty()) {
            if (messagesByPlugin.isEmpty()) return null
            val messageListForPluginIndex = messagesByPlugin.values.toList()[messagesByPluginPluginIndex]
            val res = messageListForPluginIndex[messagesByPluginMessageIndex]
            messagesByPluginMessageIndex++
            if (messagesByPluginMessageIndex == messageListForPluginIndex.size) {
                messagesByPluginMessageIndex = 0
                messagesByPluginPluginIndex = (messagesByPluginPluginIndex + 1) % messagesByPlugin.size
                globalIndex++
            }
            return res
        }

        // Use the normal list
        if (messageList.isEmpty()) return null
        val res = messageList[messageListIndex]
        messageListIndex = (messageListIndex + 1) % messageList.size

        globalIndex++
        return res
    }

    @Synchronized
    override fun add(vararg messages: Message) {
        // Do not re-add messages that are already in the list
        for (message in messages) {
            if (!messageList.contains(message)) messageList += message
        }

        // Discard old items if any
        var elementsToDiscard = 0
        if (messageList.size > size) {
            elementsToDiscard = messageList.size - size
            messageList.subList(0, elementsToDiscard).clear()
        }

        // Shift the index if elements were discarded
        messageListIndex -= elementsToDiscard
        if (messageListIndex < 0) messageListIndex = 0
    }


    @Synchronized
    override fun addUrgent(vararg messages: Message) {
        // Do not re-queue messages that are already in the list
        for (message in messages) {
            if (!urgentMessageQueue.contains(message)) urgentMessageQueue += message
        }
    }

    @Synchronized
    override fun set(plugin: Plugin, vararg message: Message) {
        messagesByPlugin[plugin] = message.asList()
    }

    @Synchronized
    override fun unset(plugin: Plugin) {
        messagesByPlugin.remove(plugin)
        messagesByPluginPluginIndex = 0
        messagesByPluginMessageIndex = 0
    }
}