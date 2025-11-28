/*
 * Copyright (c) 2023-2025 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.
 *
 * This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 * that can be found in the LICENSE file.
 */

package de.fraunhofer.isst.innamark.watermarker.watermarkers.file.unitTest

import de.fraunhofer.isst.innamark.watermarker.types.files.writeToFile
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.Watermark
import de.fraunhofer.isst.innamark.watermarker.watermarkers.file.TextFileWatermarker
import de.fraunhofer.isst.innamark.watermarker.watermarkers.text.DefaultTranscoding
import de.fraunhofer.isst.innamark.watermarker.watermarkers.text.PlainTextWatermarker
import openTextFile
import java.nio.file.Path
import kotlin.io.path.createTempFile
import kotlin.io.path.exists
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TextFileWatermarkerTestJvm {
    private val textWatermarker = TextFileWatermarker()
    private lateinit var tempFile: Path

    @BeforeTest
    fun setup() {
        tempFile = createTempFile(prefix = "temp", suffix = ".txt")
        assertTrue(tempFile.exists())
    }

    @AfterTest
    fun teardown() {
        tempFile.toFile().delete()
        assertFalse(tempFile.exists())
    }

    @Test
    fun addWatermark_valid_success() {
        // Arrange
        val filePath = "src/jvmTest/resources/lorem_ipsum.txt"
        val watermark = Watermark.fromString("Hello World")
        val expected = openTextFile("src/jvmTest/resources/lorem_ipsum_watermarked.txt")
        val expectedMessage =
            PlainTextWatermarker.Success(listOf(5, 273, 538), true).into().toString()

        // Act
        val result = textWatermarker.addWatermark(filePath, tempFile.toString(), watermark)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expected.content, openTextFile(tempFile.toString()).content)
        assertEquals(expectedMessage, result.toString())
    }

    @Test
    fun addWatermarkString_valid_success() {
        // Arrange
        val filePath = "src/jvmTest/resources/lorem_ipsum.txt"
        val watermark = "Hello World"
        val expected = openTextFile("src/jvmTest/resources/lorem_ipsum_innamarked.txt")
        val expectedMessage = PlainTextWatermarker.Success(listOf(5, 295), true).into().toString()

        // Act
        val result = textWatermarker.addWatermark(filePath, tempFile.toString(), watermark)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expected.content, openTextFile(tempFile.toString()).content)
        assertEquals(expectedMessage, result.toString())
    }

    @Test
    fun addWatermark_containsAlphabetChars_error() {
        // Arrange
        val filePath = "src/jvmTest/resources/lorem_ipsum_watermarked.txt"
        val watermarkBytes = "Hello World".encodeToByteArray()
        val watermark = Watermark(watermarkBytes)
        val expectedMessage =
            PlainTextWatermarker
                .ContainsAlphabetCharsError(
                    sequence {
                        yield(DefaultTranscoding.SEPARATOR_CHAR)
                        yieldAll(DefaultTranscoding.alphabet)
                    },
                ).into()
                .toString()

        // Act
        val result = textWatermarker.addWatermark(filePath, tempFile.toString(), watermark)

        // Assert
        assertTrue(result.isError)
        assertEquals(expectedMessage, result.toString())
    }

    @Test
    fun addWatermark_oversizedWatermark_warning() {
        // Arrange
        val filePath = "src/jvmTest/resources/lorem_ipsum.txt"
        val watermark = Watermark.fromString("This is a watermark that does not fit")
        val expectedMessage =
            PlainTextWatermarker
                .OversizedWatermarkWarning(150, 96)
                .into()
                .toString()

        // Act
        val result = textWatermarker.addWatermark(filePath, tempFile.toString(), watermark)

        // Assert
        assertTrue(result.isWarning)
        assertEquals(expectedMessage, result.toString())
    }

    @Test
    fun containsWatermark_watermark_true() {
        // Arrange
        val filePath = "src/jvmTest/resources/lorem_ipsum_watermarked.txt"

        // Act
        val result = textWatermarker.containsWatermark(filePath)

        // Assert
        assertTrue(result.hasValue)
        assertTrue(result.value!!)
    }

    @Test
    fun containsWatermark_noWatermark_false() {
        // Arrange
        val filePath = "src/jvmTest/resources/lorem_ipsum.txt"

        // Act
        val result = textWatermarker.containsWatermark(filePath)

        // Assert
        assertTrue(result.hasValue)
        assertTrue(!result.value!!)
    }

    @Test
    fun getWatermarks_watermarks_successAndWatermarks() {
        // Arrange
        val filePath = "src/jvmTest/resources/lorem_ipsum_watermarked.txt"
        val expected =
            listOf(
                Watermark.fromString("Hello World"),
                Watermark.fromString("Hello World"),
            )

        // Act
        val result = textWatermarker.getWatermarks(filePath)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expected, result.value)
    }

    @Test
    fun getWatermarks_noWatermark_successAndEmptyList() {
        // Arrange
        val filePath = "src/jvmTest/resources/lorem_ipsum.txt"

        // Act
        val result = textWatermarker.getWatermarks(filePath)

        // Assert
        assertTrue(result.isSuccess)
        assertTrue(result.value?.isEmpty() == true)
    }

    @Test
    fun getWatermarks_partialWatermark_warningAndWatermark() {
        // Arrange
        val filePath = "src/jvmTest/resources/lorem_ipsum_partial_watermark.txt"
        val expectedMessage = PlainTextWatermarker.IncompleteWatermarkWarning().into().toString()

        // Act
        val result = textWatermarker.getWatermarks(filePath)

        // Assert
        assertTrue(result.isWarning)
        assertEquals(expectedMessage, result.toString())
        assertFalse(result.value?.isEmpty() != false)
    }

    @Test
    fun removeWatermarks_watermarks_successAndCleaned() {
        // Arrange
        val sourcePath = "src/jvmTest/resources/lorem_ipsum_watermarked.txt"
        val sourceFile = openTextFile(sourcePath)
        val expectedPath = "src/jvmTest/resources/lorem_ipsum.txt"
        val expectedFile = openTextFile(expectedPath)

        // Act
        sourceFile.writeToFile(tempFile.toString())
        val result = textWatermarker.removeWatermarks(tempFile.toString())

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expectedFile, openTextFile(tempFile.toString()))
    }

    @Test
    fun removeWatermarks_noWatermark_successAndCleaned() {
        // Arrange
        val sourcePath = "src/jvmTest/resources/lorem_ipsum.txt"
        val sourceFile = openTextFile(sourcePath)

        // Act
        sourceFile.writeToFile(tempFile.toString())
        val result = textWatermarker.removeWatermarks(tempFile.toString())

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(sourceFile, openTextFile(tempFile.toString()))
    }

    @Test
    fun removeWatermarks_partialWatermark_successAndCleaned() {
        // Arrange
        val sourcePath = "src/jvmTest/resources/lorem_ipsum_partial_watermark.txt"
        val sourceFile = openTextFile(sourcePath)
        val expectedPath = "src/jvmTest/resources/lorem_ipsum.txt"
        val expectedFile = openTextFile(expectedPath)

        // Act
        sourceFile.writeToFile(tempFile.toString())
        val result = textWatermarker.removeWatermarks(tempFile.toString())

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expectedFile, openTextFile(tempFile.toString()))
    }
}
