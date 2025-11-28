/*
 * Copyright (c) 2023-2025 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.
 *
 * This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 * that can be found in the LICENSE file.
 */

package de.fraunhofer.isst.innamark.watermarker.watermarkers.file.unitTest

import de.fraunhofer.isst.innamark.watermarker.types.files.ZipFileHeader
import de.fraunhofer.isst.innamark.watermarker.types.files.writeToFile
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.Watermark
import de.fraunhofer.isst.innamark.watermarker.watermarkers.file.ZipFileWatermarker
import openZipFile
import java.nio.file.Path
import kotlin.io.path.createTempFile
import kotlin.io.path.exists
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ZipFileWatermarkerTestJvm {
    private val zipWatermarker = ZipFileWatermarker
    private lateinit var tempFile: Path

    @BeforeTest
    fun setup() {
        tempFile = createTempFile(prefix = "temp", suffix = ".zip")
        assertTrue(tempFile.exists())
    }

    @AfterTest
    fun teardown() {
        tempFile.toFile().delete()
        assertFalse(tempFile.exists())
    }

    @Test
    fun addWatermark_valid_successNoWrap() {
        // Arrange
        val sourcePath = "src/jvmTest/resources/multiple_files.zip"
        val sourceFile = openZipFile(sourcePath)
        val watermarkText = "Lorem ipsum dolor sit amet"
        val watermark = watermarkText.encodeToByteArray()
        val expected = openZipFile("src/jvmTest/resources/multiple_files_watermarked.zip")

        // Act
        sourceFile.writeToFile(tempFile.toString())
        val result = zipWatermarker.addWatermark(sourcePath, tempFile.toString(), watermark, false)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expected, openZipFile(tempFile.toString()))
    }

    @Test
    fun addWatermark_valid_successAndWrap() {
        // Arrange
        val sourcePath = "src/jvmTest/resources/multiple_files.zip"
        val sourceFile = openZipFile(sourcePath)
        val watermarkText = "Lorem ipsum dolor sit amet"
        val watermark = watermarkText.encodeToByteArray()
        val expected = openZipFile("src/jvmTest/resources/multiple_files_innamarked.zip")

        // Act
        sourceFile.writeToFile(tempFile.toString())
        val result = zipWatermarker.addWatermark(sourcePath, tempFile.toString(), watermark, true)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expected, openZipFile(tempFile.toString()))
    }

    @Test
    fun addWatermark_oversizedWatermark_error() {
        // Arrange
        val sourcePath = "src/jvmTest/resources/multiple_files.zip"
        val sourceFile = openZipFile(sourcePath)
        val watermark = Watermark(ByteArray(UShort.MAX_VALUE.toInt()) { 0 })
        val expectedMessage =
            ZipFileHeader.ExtraField
                .OversizedHeaderError(65567)
                .into()
                .toString()

        // Act
        sourceFile.writeToFile(tempFile.toString())
        val status =
            zipWatermarker.addWatermark(
                tempFile.toString(),
                tempFile.toString(),
                watermark,
            )

        // Assert
        assertTrue(status.isError)
        assertEquals(expectedMessage, status.toString())
    }

    @Test
    fun containsWatermark_watermark_true() {
        // Arrange
        val sourcePath = "src/jvmTest/resources/multiple_files_watermarked.zip"

        // Act
        val result = zipWatermarker.containsWatermark(sourcePath)

        // Assert
        assertTrue(result.value ?: false)
    }

    @Test
    fun containsWatermark_noWatermark_false() {
        // Arrange
        val sourcePath = "src/jvmTest/resources/multiple_files.zip"

        // Act
        val result = zipWatermarker.containsWatermark(sourcePath)

        // Assert
        assertFalse(result.value ?: true)
    }

    @Test
    fun getWatermarks_watermark_successAndWatermark() {
        // Arrange
        val sourcePath = "src/jvmTest/resources/multiple_files_watermarked.zip"
        val sourceFile = openZipFile(sourcePath)
        val expected = listOf(Watermark.fromString("Lorem ipsum dolor sit amet"))

        // Act
        sourceFile.writeToFile(tempFile.toString())
        val result = zipWatermarker.getWatermarks(tempFile.toString())

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expected, result.value)
    }

    @Test
    fun getWatermarks_noWatermark_successAndEmptyList() {
        // Arrange
        val sourcePath = "src/jvmTest/resources/multiple_files.zip"
        val sourceFile = openZipFile(sourcePath)

        // Act
        sourceFile.writeToFile(tempFile.toString())
        val result = zipWatermarker.getWatermarks(tempFile.toString())

        // Assert
        assertTrue(result.isSuccess)
        assertTrue(result.value?.isEmpty() ?: false)
    }

    @Test
    fun removeWatermarks_watermark_successAndCleaned() {
        // Arrange
        val sourcePath = "src/jvmTest/resources/multiple_files_watermarked.zip"
        val sourceFile = openZipFile(sourcePath)
        val expectedPath = "src/jvmTest/resources/multiple_files.zip"
        val expectedFile = openZipFile(expectedPath)

        // Act
        sourceFile.writeToFile(tempFile.toString())
        val result = zipWatermarker.removeWatermarks(tempFile.toString())
        val resultFile = openZipFile(tempFile.toString())

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expectedFile, resultFile)
    }

    @Test
    fun removeWatermarks_noWatermark_successAndCleaned() {
        // Arrange
        val sourcePath = "src/jvmTest/resources/multiple_files.zip"
        val sourceFile = openZipFile(sourcePath)

        // Act
        sourceFile.writeToFile(tempFile.toString())
        val result = zipWatermarker.removeWatermarks(tempFile.toString())
        val resultFile = openZipFile(tempFile.toString())

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(sourceFile, resultFile)
    }
}
