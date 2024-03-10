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

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.ResourceBundle
import kotlin.concurrent.thread

abstract class PeriodicPlugin : BasePlugin() {
    private var started = false

    abstract val periodMs: Long
    open val initialDelayMs: Long = 0

    val resourceBundle: ResourceBundle by lazy {
        ResourceBundle.getBundle(javaClass.name)
    }

    override fun start() {
        super.start()
        started = true
        thread {
            Thread.sleep(500)
            while (started) {
                try {
                    queueMessage()
                } catch (t: Throwable) {
                    LOGGER.warn("Exception caught when calling queueMessage from ${this.javaClass}", t)
                }
                Thread.sleep(periodMs)
            }
        }
    }

    abstract fun queueMessage()

    override fun stop() {
        started = false
        super.stop()
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(PeriodicPlugin::class.java)
    }
}
