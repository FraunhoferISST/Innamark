/*
 * SPDX-FileCopyrightText: 2024-2026 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e. V.
 *
 * SPDX-License-Identifier: LicenseRef-Fraunhofer-Gesellschaft
 *
 * This code is based on the C implementation of the RFC 1952 standard:
 * https://datatracker.ietf.org/doc/html/rfc1952#section-8
 */

package de.fraunhofer.isst.innamark.watermarker.utils

actual object CRC32 {
    private val table =
        (0u..255u)
            .map { n ->
                var c = n
                repeat(8) {
                    c =
                        if (c and 1u != 0u) {
                            0xedb88320u xor (c shr 1)
                        } else {
                            c shr 1
                        }
                }
                c
            }.toList()

    /** Calculates the CRC32 checksum of [bytes] */
    actual fun checksum(bytes: ByteArray): UInt {
        var c = 0xffffffffu
        for (byte in bytes) {
            val index = (c xor byte.toUInt() and 0xffu).toInt()
            c = table[index] xor (c shr 8)
        }
        return c xor 0xffffffffu
    }
}
