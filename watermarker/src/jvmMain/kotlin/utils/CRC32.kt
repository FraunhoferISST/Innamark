/*
 * SPDX-FileCopyrightText: 2024-2026 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e. V.
 *
 * SPDX-License-Identifier: LicenseRef-Fraunhofer-Gesellschaft
 */

package de.fraunhofer.isst.innamark.watermarker.utils

import java.util.zip.CRC32

actual object CRC32 {
    actual fun checksum(bytes: ByteArray): UInt {
        val crc = CRC32()
        crc.update(bytes)
        return crc.value.toUInt()
    }
}
