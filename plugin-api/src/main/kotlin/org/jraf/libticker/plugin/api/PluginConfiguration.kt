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

package org.jraf.libticker.plugin.api

class PluginConfiguration(vararg keyValues: Pair<String, Any>) {

    private sealed class ItemValue<T>(
        val value: T
    ) {
        internal class StringItemValue(value: String) : ItemValue<String>(value)
        internal class NumberItemValue(value: Number) : ItemValue<Number>(value)
        internal class BooleanItemValue(value: Boolean) : ItemValue<Boolean>(value)
    }

    private val items = mutableMapOf<String, ItemValue<*>>()

    init {
        put(*keyValues)
    }

    fun put(key: String, value: String) {
        items[key] = ItemValue.StringItemValue(value)
    }

    fun put(key: String, value: Number) {
        items[key] = ItemValue.NumberItemValue(value)
    }

    fun put(key: String, value: Boolean) {
        items[key] = ItemValue.BooleanItemValue(value)
    }

    fun put(vararg keyValues: Pair<String, Any>) {
        for (keyValue in keyValues) {
            val key = keyValue.first
            val value = keyValue.second
            when (value) {
                is String -> put(key, value)
                is Number -> put(key, value)
                is Boolean -> put(key, value)
                else -> throw IllegalArgumentException("'$value' must be either a String, a Number or a Boolean")
            }
        }
    }

    /**
     * @throws IllegalArgumentException if the given [key] can't be found or doesn't map to a String
     */
    fun getString(key: String): String {
        val res = (items[key] ?: throw IllegalArgumentException("'$key' not found")) as? ItemValue.StringItemValue
            ?: throw IllegalArgumentException("'$key' is not a String")
        return res.value
    }

    /**
     * @throws IllegalArgumentException if the given [key] doesn't map to a String
     */
    fun getStringOrNull(key: String): String? {
        val res = (items[key] ?: return null) as? ItemValue.StringItemValue
            ?: throw IllegalArgumentException("'$key' is not a String")
        return res.value
    }

    /**
     * @throws IllegalArgumentException if the given [key] can't be found or doesn't map to a Number
     */
    fun getNumber(key: String): Number {
        val res = (items[key] ?: throw IllegalArgumentException("'$key' not found")) as? ItemValue.NumberItemValue
            ?: throw IllegalArgumentException("'$key' is not a Number")
        return res.value
    }

    /**
     * @throws IllegalArgumentException if the given [key] doesn't map to a Number
     */
    fun getNumberOrNull(key: String): Number? {
        val res = (items[key] ?: return null) as? ItemValue.NumberItemValue
            ?: throw IllegalArgumentException("'$key' is not a Number")
        return res.value
    }

    /**
     * @throws IllegalArgumentException if the given [key] can't be found or doesn't map to a Boolean
     */
    fun getBoolean(key: String): Boolean {
        val res = (items[key] ?: throw IllegalArgumentException("'$key' not found")) as? ItemValue.BooleanItemValue
            ?: throw IllegalArgumentException("'$key' is not a Boolean")
        return res.value
    }

    /**
     * @throws IllegalArgumentException if the given [key] doesn't map to a Boolean
     */
    fun getBooleanOrNull(key: String): Boolean? {
        val res = (items[key] ?: return null) as? ItemValue.BooleanItemValue
            ?: throw IllegalArgumentException("'$key' is not a Boolean")
        return res.value
    }

    /**
     * @throws IllegalArgumentException if the given [key] can't be found
     */
    operator fun get(key: String): Any {
        return items[key]?.value ?: throw IllegalArgumentException("'$key' not found")
    }

    fun containsKey(key: String): Boolean = items.containsKey(key)

    fun remove(key: String) {
        items.remove(key)
    }

    fun clear() = items.clear()

    val keys get() = items.keys

    val asMap: Map<String, Any> get() = items.mapValues { it.value.value!! }

    override fun toString() = items.toString()
}
