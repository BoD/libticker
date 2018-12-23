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

package org.jraf.libticker.httpconf

import java.net.InetAddress
import java.net.NetworkInterface

/**
 * Returns an `InetAddress` object encapsulating what is most likely the machine's LAN IP address.
 *
 * This method is intended for use as a replacement of JDK method `InetAddress.getLocalHost`, because
 * that method is ambiguous on Linux systems. Linux systems enumerate the loopback network interface the same
 * way as regular LAN network interfaces, but the JDK `InetAddress.getLocalHost` method does not
 * specify the algorithm used to select the address returned under such circumstances, and will often return the
 * loopback address, which is not valid for network communication. Details
 * [here](http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4665037).
 *
 * This method will scan all IP addresses on all network interfaces on the host machine to determine the IP address
 * most likely to be the machine's LAN address. If the machine has multiple IP addresses, this method will prefer
 * a site-local IP address (e.g. 192.168.x.x or 10.10.x.x, usually IPv4) if the machine has one (and will return the
 * first site-local address if the machine has more than one), but if the machine does not hold a site-local
 * address, this method will return simply the first non-loopback address found (IPv4 or IPv6).
 *
 * If this method cannot find a non-loopback address using this selection algorithm, it will fall back to
 * calling and returning the result of JDK method `InetAddress.getLocalHost`.
 */
internal fun getLocalHostLanAddress(): InetAddress? {
    try {
        var candidateAddress: InetAddress? = null
        // Iterate all NICs (network interface cards)...
        val ifaces = NetworkInterface.getNetworkInterfaces()
        while (ifaces.hasMoreElements()) {
            val iface = ifaces.nextElement() as NetworkInterface
            // Iterate all IP addresses assigned to each card...
            val inetAddrs = iface.inetAddresses
            while (inetAddrs.hasMoreElements()) {
                val inetAddr = inetAddrs.nextElement() as InetAddress
                if (!inetAddr.isLoopbackAddress) {
                    if (inetAddr.isSiteLocalAddress) {
                        // Found non-loopback site-local address. Return it immediately...
                        return inetAddr
                    } else if (candidateAddress == null) {
                        // Found non-loopback address, but not necessarily site-local.
                        // Store it as a candidate to be returned if site-local address is not subsequently found...
                        candidateAddress = inetAddr
                        // Note that we don't repeatedly assign non-loopback non-site-local addresses as candidates,
                        // only the first. For subsequent iterations, candidate will be non-null.
                    }
                }
            }
        }
        // At this point, we did not find a non-loopback address.
        // Fall back to returning whatever InetAddress.getLocalHost() returns...
        return candidateAddress ?: InetAddress.getLocalHost()
    } catch (e: Exception) {
        return null
    }
}
