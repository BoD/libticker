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

package org.jraf.libticker.plugin

import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.jraf.libticker.message.MessageQueue
import org.jraf.libticker.plugin.api.Plugin
import org.jraf.libticker.plugin.api.PluginConfiguration
import java.util.concurrent.TimeUnit

abstract class PeriodicPlugin : Plugin {
    lateinit var messageQueue: MessageQueue
    abstract val periodMs: Long
    open val initialDelayMs: Long = 0

    private var taskDisposable: Disposable? = null

    override fun init(messageQueue: MessageQueue, configuration: PluginConfiguration?) {
        this.messageQueue = messageQueue;
    }

    override fun start() {
        taskDisposable = Schedulers.computation().schedulePeriodicallyDirect({ queueMessage() },
                initialDelayMs, periodMs, TimeUnit.MILLISECONDS)
    }

    abstract fun queueMessage()

    override fun stop() {
        taskDisposable?.dispose()
    }
}