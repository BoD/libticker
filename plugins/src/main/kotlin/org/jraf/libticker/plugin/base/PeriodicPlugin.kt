/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2018-present Benoit 'BoD' Lubek (BoD@JRAF.org)
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

package org.jraf.libticker.plugin.base

import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.ResourceBundle
import java.util.concurrent.TimeUnit

abstract class PeriodicPlugin : BasePlugin() {
    abstract val periodMs: Long
    open val initialDelayMs: Long = 0

    val resourceBundle: ResourceBundle by lazy {
        ResourceBundle.getBundle(javaClass.name)
    }

    private var taskDisposable: Disposable? = null

    override fun start() {
        super.start()
        taskDisposable = Schedulers.computation().schedulePeriodicallyDirect(
            { queueMessage() },
            // Add a few milliseconds because sometimes we are called too early!
            initialDelayMs + 500,
            periodMs,
            TimeUnit.MILLISECONDS
        )
    }

    abstract fun queueMessage()

    override fun stop() {
        taskDisposable?.dispose()
        super.stop()
    }
}