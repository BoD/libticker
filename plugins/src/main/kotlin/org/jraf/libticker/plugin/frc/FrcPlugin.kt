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

package org.jraf.libticker.plugin.frc

import ca.rmen.lfrc.FrenchRevolutionaryCalendar
import org.jraf.libticker.message.Message
import org.jraf.libticker.plugin.base.PeriodicPlugin
import org.jraf.libticker.plugin.frc.FrcPluginDescriptor.KEY_PERIOD
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class FrcPlugin : PeriodicPlugin() {
    override val descriptor = FrcPluginDescriptor.DESCRIPTOR

    override val periodMs get() = TimeUnit.MINUTES.toMillis(configuration.getNumber(KEY_PERIOD).toLong())

    override fun queueMessage() {
        val frcDate =
            FrenchRevolutionaryCalendar(Locale.FRENCH, FrenchRevolutionaryCalendar.CalculationMethod.ROMME).getDate(
                Calendar.getInstance() as GregorianCalendar
            )
        val frcDateStr = resourceBundle.getString("frc_date")
            .format(frcDate.weekdayName, frcDate.dayOfMonth, frcDate.monthName, frcDate.year)
        val frcObjectStr = resourceBundle.getString("frc_object").format(frcDate.objectTypeName, frcDate.objectOfTheDay)

        messageQueue.set(this, Message(frcDateStr), Message(frcObjectStr))
    }
}