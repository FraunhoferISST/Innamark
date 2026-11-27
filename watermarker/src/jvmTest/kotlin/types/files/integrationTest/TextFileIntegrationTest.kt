/*
 * SPDX-FileCopyrightText: 2023-2026 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e. V.
 *
 * SPDX-License-Identifier: LicenseRef-Fraunhofer-Gesellschaft
 */

package de.fraunhofer.isst.innamark.watermarker.types.files.integrationTest

import de.fraunhofer.isst.innamark.watermarker.types.files.TextFile
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TextFileIntegrationTest {
    private val loremIpsum =
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor " +
            "incididunt ut labore et dolore magna aliqua. Blandit volutpat maecenas " +
            "volutpat blandit aliquam etiam erat velit."
    private val loremIpsumBytes = loremIpsum.encodeToByteArray()

    @Test
    fun toBytes_fromBytes_inversion() {
        // Act
        val result = TextFile.Companion.fromBytes(loremIpsumBytes)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(loremIpsum, result.value?.content)
    }
}
