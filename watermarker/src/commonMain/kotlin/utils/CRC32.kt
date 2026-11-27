/*
 * SPDX-FileCopyrightText: 2024-2026 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e. V.
 *
 * SPDX-License-Identifier: LicenseRef-Fraunhofer-Gesellschaft
 */

package de.fraunhofer.isst.innamark.watermarker.utils

expect object CRC32 {
    /** Calculates the CRC32 checksum of [bytes] */
    fun checksum(bytes: ByteArray): UInt
}
