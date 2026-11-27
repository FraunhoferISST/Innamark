/*
 * SPDX-FileCopyrightText: 2023-2026 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e. V.
 *
 * SPDX-License-Identifier: LicenseRef-Fraunhofer-Gesellschaft
 */

package de.fraunhofer.isst.innamark.watermarker.utils.integrationTest

import de.fraunhofer.isst.innamark.watermarker.utils.fromBytesLittleEndian
import de.fraunhofer.isst.innamark.watermarker.utils.toBytesLittleEndian
import kotlin.test.Test
import kotlin.test.assertEquals

class ExtensionFunctionsIntegrationTest {
    @Test
    fun uIntToBytesLittleEndian_UIntCompanionFromBytesLittleEndian_inversion() {
        // Arrange
        val uInt = 0x12345678u

        // Act
        val result = UInt.Companion.fromBytesLittleEndian(uInt.toBytesLittleEndian())

        // Assert
        assertEquals(uInt, result)
    }

    @Test
    fun uShortToBytesLittleEndian_UShortCompanionFromBytesLittleEndian_inversion() {
        // Arrange
        val uShort = 0x1234u.toUShort()

        // Act
        val result = UShort.fromBytesLittleEndian(uShort.toBytesLittleEndian())

        // Assert
        assertEquals(uShort, result)
    }
}
