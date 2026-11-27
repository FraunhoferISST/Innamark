/*
 * SPDX-FileCopyrightText: 2023-2026 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e. V.
 *
 * SPDX-License-Identifier: LicenseRef-Fraunhofer-Gesellschaft
 */

package de.fraunhofer.isst.innamark.watermarker.types.files.unitTest

import de.fraunhofer.isst.innamark.watermarker.types.files.WatermarkableFile
import kotlin.test.Test
import kotlin.test.assertEquals

class WatermarkableFileTest {
    @Test
    fun readError_string_success() {
        // Arrange
        val error = WatermarkableFile.ReadError("/some/path", "some reason")
        val expected = "Error (File.read): some reason"

        // Act
        val result = error.toString()

        // Assert
        assertEquals(expected, result)
    }

    @Test
    fun writeError_string_success() {
        // Arrange
        val error = WatermarkableFile.WriteError("/some/path", "some reason")
        val expected = "Error (File.write): some reason"

        // Act
        val result = error.toString()

        // Assert
        assertEquals(expected, result)
    }
}
