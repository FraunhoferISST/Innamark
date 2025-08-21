/*
 * Copyright (c) 2024-2025 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.
 *
 * This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 * that can be found in the LICENSE file.
 */
package unitTest.watermarks

import de.fraunhofer.isst.innamark.watermarker.watermarks.CRC32InnamarkTag
import de.fraunhofer.isst.innamark.watermarker.watermarks.CompressedCRC32InnamarkTag
import de.fraunhofer.isst.innamark.watermarker.watermarks.CompressedRawInnamarkTag
import de.fraunhofer.isst.innamark.watermarker.watermarks.CompressedSHA3256InnamarkTag
import de.fraunhofer.isst.innamark.watermarker.watermarks.CompressedSizedCRC32InnamarkTag
import de.fraunhofer.isst.innamark.watermarker.watermarks.CompressedSizedInnamarkTag
import de.fraunhofer.isst.innamark.watermarker.watermarks.CompressedSizedSHA3256InnamarkTag
import de.fraunhofer.isst.innamark.watermarker.watermarks.RawInnamarkTag
import de.fraunhofer.isst.innamark.watermarker.watermarks.SHA3256InnamarkTag
import de.fraunhofer.isst.innamark.watermarker.watermarks.SizedCRC32InnamarkTag
import de.fraunhofer.isst.innamark.watermarker.watermarks.SizedInnamarkTag
import de.fraunhofer.isst.innamark.watermarker.watermarks.SizedSHA3256InnamarkTag
import de.fraunhofer.isst.innamark.watermarker.watermarks.TextWatermark
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TextWatermarkTest {
    private val text = "Lorem Ipsum"
    private val textBytes = text.encodeToByteArray()

    @Test
    fun new_loremIpsum_success() {
        // Arrange
        val expectedInnamarkTag = RawInnamarkTag.new(textBytes)

        // Act
        val textWatermark = TextWatermark.new(text)
        val innamarkTag = textWatermark.finish()
        val content = innamarkTag.getContent()

        // Assert
        assertEquals(text, textWatermark.text)
        assertFalse(textWatermark.isSized())
        assertFalse(textWatermark.isCompressed())
        assertFalse(textWatermark.isCRC32())
        assertFalse(textWatermark.isSHA3256())
        assertEquals(expectedInnamarkTag, innamarkTag)
        assertTrue(content.isSuccess)
        assertContentEquals(textBytes, content.value)
    }

    @Test
    fun raw_loremIpsum_success() {
        // Arrange
        val expectedInnamarkTag = RawInnamarkTag.new(textBytes)

        // Act
        val textWatermark = TextWatermark.raw(text)
        val innamarkTag = textWatermark.finish()
        val content = innamarkTag.getContent()

        // Assert
        assertEquals(text, textWatermark.text)
        assertFalse(textWatermark.isSized())
        assertFalse(textWatermark.isCompressed())
        assertFalse(textWatermark.isCRC32())
        assertFalse(textWatermark.isSHA3256())
        assertEquals(expectedInnamarkTag, innamarkTag)
        assertTrue(content.isSuccess)
        assertContentEquals(textBytes, content.value)
    }

    @Test
    fun compressed_loremIpsum_success() {
        // Arrange
        val expectedInnamarkTag = CompressedRawInnamarkTag.new(textBytes)

        // Act
        val textWatermark = TextWatermark.compressed(text)
        val innamarkTag = textWatermark.finish()
        val content = innamarkTag.getContent()

        // Assert
        assertEquals(text, textWatermark.text)
        assertFalse(textWatermark.isSized())
        assertTrue(textWatermark.isCompressed())
        assertFalse(textWatermark.isCRC32())
        assertFalse(textWatermark.isSHA3256())
        assertEquals(expectedInnamarkTag, innamarkTag)
        assertTrue(content.isSuccess)
        assertContentEquals(textBytes, content.value)
    }

    @Test
    fun small_loremIpsum_compression() {
        // Arrange
        val customText = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr"
        val customTextBytes = customText.encodeToByteArray()
        val expectedInnamarkTag = CompressedRawInnamarkTag.new(customTextBytes)

        // Act
        val textWatermark = TextWatermark.small(customText)
        val innamarkTag = textWatermark.finish()
        val content = innamarkTag.getContent()

        // Assert
        assertEquals(customText, textWatermark.text)
        assertFalse(textWatermark.isSized())
        assertTrue(textWatermark.isCompressed())
        assertFalse(textWatermark.isCRC32())
        assertFalse(textWatermark.isSHA3256())
        assertEquals(expectedInnamarkTag, innamarkTag)
        assertTrue(content.isSuccess)
        assertContentEquals(customTextBytes, content.value)
    }

    @Test
    fun small_loremIpsum_noCompression() {
        // Arrange
        val customText = "Lorem"
        val customTextBytes = customText.encodeToByteArray()
        val expectedInnamarkTag = RawInnamarkTag.new(customTextBytes)

        // Act
        val textWatermark = TextWatermark.small(customText)
        val innamarkTag = textWatermark.finish()
        val content = innamarkTag.getContent()

        // Assert
        assertEquals(customText, textWatermark.text)
        assertFalse(textWatermark.isSized())
        assertFalse(textWatermark.isCompressed())
        assertFalse(textWatermark.isCRC32())
        assertFalse(textWatermark.isSHA3256())
        assertEquals(expectedInnamarkTag, innamarkTag)
        assertTrue(content.isSuccess)
        assertContentEquals(customTextBytes, content.value)
    }

    @Test
    fun sized_loremIpsum_success() {
        // Arrange
        val expectedInnamarkTag = SizedInnamarkTag.new(textBytes)

        // Act
        val textWatermark = TextWatermark.sized(text)
        val innamarkTag = textWatermark.finish()
        val content = innamarkTag.getContent()

        // Assert
        assertEquals(text, textWatermark.text)
        assertTrue(textWatermark.isSized())
        assertFalse(textWatermark.isCompressed())
        assertFalse(textWatermark.isCRC32())
        assertFalse(textWatermark.isSHA3256())
        assertEquals(expectedInnamarkTag, innamarkTag)
        assertTrue(content.isSuccess)
        assertContentEquals(textBytes, content.value)
    }

    @Test
    @Suppress("ktlint:standard:function-naming")
    fun CRC32_loremIpsum_success() {
        // Arrange
        val expectedInnamarkTag = CRC32InnamarkTag.new(textBytes)

        // Act
        val textWatermark = TextWatermark.CRC32(text)
        val innamarkTag = textWatermark.finish()
        val content = innamarkTag.getContent()

        // Assert
        assertEquals(text, textWatermark.text)
        assertFalse(textWatermark.isSized())
        assertFalse(textWatermark.isCompressed())
        assertTrue(textWatermark.isCRC32())
        assertFalse(textWatermark.isSHA3256())
        assertEquals(expectedInnamarkTag, innamarkTag)
        assertTrue(content.isSuccess)
        assertContentEquals(textBytes, content.value)
    }

    @Test
    @Suppress("ktlint:standard:function-naming")
    fun SHA3256_loremIpsum_success() {
        // Arrange
        val expectedInnamarkTag = SHA3256InnamarkTag.new(textBytes)

        // Act
        val textWatermark = TextWatermark.SHA3256(text)
        val innamarkTag = textWatermark.finish()
        val content = innamarkTag.getContent()

        // Assert
        assertEquals(text, textWatermark.text)
        assertFalse(textWatermark.isSized())
        assertFalse(textWatermark.isCompressed())
        assertFalse(textWatermark.isCRC32())
        assertTrue(textWatermark.isSHA3256())
        assertEquals(expectedInnamarkTag, innamarkTag)
        assertTrue(content.isSuccess)
        assertContentEquals(textBytes, content.value)
    }

    @Test
    fun compressedSized_loremIpsum_success() {
        // Arrange
        val expectedInnamarkTag = CompressedSizedInnamarkTag.new(textBytes)

        // Act
        val textWatermark = TextWatermark.compressedSized(text)
        val innamarkTag = textWatermark.finish()
        val content = innamarkTag.getContent()

        // Assert
        assertEquals(text, textWatermark.text)
        assertTrue(textWatermark.isSized())
        assertTrue(textWatermark.isCompressed())
        assertFalse(textWatermark.isCRC32())
        assertFalse(textWatermark.isSHA3256())
        assertEquals(expectedInnamarkTag, innamarkTag)
        assertTrue(content.isSuccess)
        assertContentEquals(textBytes, content.value)
    }

    @Test
    fun compressedCRC32_loremIpsum_success() {
        // Arrange
        val expectedInnamarkTag = CompressedCRC32InnamarkTag.new(textBytes)

        // Act
        val textWatermark = TextWatermark.compressedCRC32(text)
        val innamarkTag = textWatermark.finish()
        val content = innamarkTag.getContent()

        // Assert
        assertEquals(text, textWatermark.text)
        assertFalse(textWatermark.isSized())
        assertTrue(textWatermark.isCompressed())
        assertTrue(textWatermark.isCRC32())
        assertFalse(textWatermark.isSHA3256())
        assertEquals(expectedInnamarkTag, innamarkTag)
        assertTrue(content.isSuccess)
        assertContentEquals(textBytes, content.value)
    }

    @Test
    fun compressedSHA3256_loremIpsum_success() {
        // Arrange
        val expectedInnamarkTag = CompressedSHA3256InnamarkTag.new(textBytes)

        // Act
        val textWatermark = TextWatermark.compressedSHA3256(text)
        val innamarkTag = textWatermark.finish()
        val content = innamarkTag.getContent()

        // Assert
        assertEquals(text, textWatermark.text)
        assertFalse(textWatermark.isSized())
        assertTrue(textWatermark.isCompressed())
        assertFalse(textWatermark.isCRC32())
        assertTrue(textWatermark.isSHA3256())
        assertEquals(expectedInnamarkTag, innamarkTag)
        assertTrue(content.isSuccess)
        assertContentEquals(textBytes, content.value)
    }

    @Test
    fun sizedCRC32_loremIpsum_success() {
        // Arrange
        val expectedInnamarkTag = SizedCRC32InnamarkTag.new(textBytes)

        // Act
        val textWatermark = TextWatermark.sizedCRC32(text)
        val innamarkTag = textWatermark.finish()
        val content = innamarkTag.getContent()

        // Assert
        assertEquals(text, textWatermark.text)
        assertTrue(textWatermark.isSized())
        assertFalse(textWatermark.isCompressed())
        assertTrue(textWatermark.isCRC32())
        assertFalse(textWatermark.isSHA3256())
        assertEquals(expectedInnamarkTag, innamarkTag)
        assertTrue(content.isSuccess)
        assertContentEquals(textBytes, content.value)
    }

    @Test
    fun sizedSHA3256_loremIpsum_success() {
        // Arrange
        val expectedInnamarkTag = SizedSHA3256InnamarkTag.new(textBytes)

        // Act
        val textWatermark = TextWatermark.sizedSHA3256(text)
        val innamarkTag = textWatermark.finish()
        val content = innamarkTag.getContent()

        // Assert
        assertEquals(text, textWatermark.text)
        assertTrue(textWatermark.isSized())
        assertFalse(textWatermark.isCompressed())
        assertFalse(textWatermark.isCRC32())
        assertTrue(textWatermark.isSHA3256())
        assertEquals(expectedInnamarkTag, innamarkTag)
        assertTrue(content.isSuccess)
        assertContentEquals(textBytes, content.value)
    }

    @Test
    fun compressedSizedCRC32_loremIpsum_success() {
        // Arrange
        val expectedInnamarkTag = CompressedSizedCRC32InnamarkTag.new(textBytes)

        // Act
        val textWatermark = TextWatermark.compressedSizedCRC32(text)
        val innamarkTag = textWatermark.finish()
        val content = innamarkTag.getContent()

        // Assert
        assertEquals(text, textWatermark.text)
        assertTrue(textWatermark.isSized())
        assertTrue(textWatermark.isCompressed())
        assertTrue(textWatermark.isCRC32())
        assertFalse(textWatermark.isSHA3256())
        assertEquals(expectedInnamarkTag, innamarkTag)
        assertTrue(content.isSuccess)
        assertContentEquals(textBytes, content.value)
    }

    @Test
    fun compressedSizedSHA3256_loremIpsum_success() {
        // Arrange
        val expectedInnamarkTag = CompressedSizedSHA3256InnamarkTag.new(textBytes)

        // Act
        val textWatermark = TextWatermark.compressedSizedSHA3256(text)
        val innamarkTag = textWatermark.finish()
        val content = innamarkTag.getContent()

        // Assert
        assertEquals(text, textWatermark.text)
        assertTrue(textWatermark.isSized())
        assertTrue(textWatermark.isCompressed())
        assertFalse(textWatermark.isCRC32())
        assertTrue(textWatermark.isSHA3256())
        assertEquals(expectedInnamarkTag, innamarkTag)
        assertTrue(content.isSuccess)
        assertContentEquals(textBytes, content.value)
    }

    @Test
    fun fromInnamarkTag_RawInnamarkTag_success() {
        // Arrange
        val initialInnamarkTag = RawInnamarkTag.new(textBytes)

        // Act
        val textWatermarkResult = TextWatermark.fromInnamarkTag(initialInnamarkTag)
        val innamarkTag = textWatermarkResult.value?.finish()

        // Assert
        assertTrue(textWatermarkResult.isSuccess)
        val textWatermark = textWatermarkResult.value!!
        assertEquals(text, textWatermark.text)
        assertFalse(textWatermark.isSized())
        assertFalse(textWatermark.isCompressed())
        assertFalse(textWatermark.isCRC32())
        assertFalse(textWatermark.isSHA3256())
        assertNotNull(innamarkTag)
        assertEquals(initialInnamarkTag, innamarkTag)
    }

    @Test
    fun fromInnamarkTag_SizedInnamarkTag_success() {
        // Arrange
        val initialInnamarkTag = SizedInnamarkTag.new(textBytes)

        // Act
        val textWatermarkResult = TextWatermark.fromInnamarkTag(initialInnamarkTag)
        val innamarkTag = textWatermarkResult.value?.finish()

        // Assert
        assertTrue(textWatermarkResult.isSuccess)
        val textWatermark = textWatermarkResult.value!!
        assertEquals(text, textWatermark.text)
        assertTrue(textWatermark.isSized())
        assertFalse(textWatermark.isCompressed())
        assertFalse(textWatermark.isCRC32())
        assertFalse(textWatermark.isSHA3256())
        assertNotNull(innamarkTag)
        assertEquals(initialInnamarkTag, innamarkTag)
    }

    @Test
    fun fromInnamarkTag_CompressedSizedInnamarkTag_success() {
        // Arrange
        val initialInnamarkTag = CompressedSizedInnamarkTag.new(textBytes)

        // Act
        val textWatermarkResult = TextWatermark.fromInnamarkTag(initialInnamarkTag)
        val innamarkTag = textWatermarkResult.value?.finish()

        // Assert
        assertTrue(textWatermarkResult.isSuccess)
        val textWatermark = textWatermarkResult.value!!
        assertEquals(text, textWatermark.text)
        assertTrue(textWatermark.isSized())
        assertTrue(textWatermark.isCompressed())
        assertFalse(textWatermark.isCRC32())
        assertFalse(textWatermark.isSHA3256())
        assertNotNull(innamarkTag)
        assertEquals(initialInnamarkTag, innamarkTag)
    }

    @Test
    fun fromInnamarkTag_CRC32InnamarkTag_success() {
        // Arrange
        val initialInnamarkTag = CRC32InnamarkTag.new(textBytes)

        // Act
        val textWatermarkResult = TextWatermark.fromInnamarkTag(initialInnamarkTag)
        val innamarkTag = textWatermarkResult.value?.finish()

        // Assert
        assertTrue(textWatermarkResult.isSuccess)
        val textWatermark = textWatermarkResult.value!!
        assertEquals(text, textWatermark.text)
        assertFalse(textWatermark.isSized())
        assertFalse(textWatermark.isCompressed())
        assertTrue(textWatermark.isCRC32())
        assertFalse(textWatermark.isSHA3256())
        assertNotNull(innamarkTag)
        assertEquals(initialInnamarkTag, innamarkTag)
    }

    @Test
    fun fromInnamarkTag_SizedCRC32InnamarkTag_success() {
        // Arrange
        val initialInnamarkTag = SizedCRC32InnamarkTag.new(textBytes)

        // Act
        val textWatermarkResult = TextWatermark.fromInnamarkTag(initialInnamarkTag)
        val innamarkTag = textWatermarkResult.value?.finish()

        // Assert
        assertTrue(textWatermarkResult.isSuccess)
        val textWatermark = textWatermarkResult.value!!
        assertEquals(text, textWatermark.text)
        assertTrue(textWatermark.isSized())
        assertFalse(textWatermark.isCompressed())
        assertTrue(textWatermark.isCRC32())
        assertFalse(textWatermark.isSHA3256())
        assertNotNull(innamarkTag)
        assertEquals(initialInnamarkTag, innamarkTag)
    }

    @Test
    fun fromInnamarkTag_CompressedCRC32InnamarkTag_success() {
        // Arrange
        val initialInnamarkTag = CompressedCRC32InnamarkTag.new(textBytes)

        // Act
        val textWatermarkResult = TextWatermark.fromInnamarkTag(initialInnamarkTag)
        val innamarkTag = textWatermarkResult.value?.finish()

        // Assert
        assertTrue(textWatermarkResult.isSuccess)
        val textWatermark = textWatermarkResult.value!!
        assertEquals(text, textWatermark.text)
        assertFalse(textWatermark.isSized())
        assertTrue(textWatermark.isCompressed())
        assertTrue(textWatermark.isCRC32())
        assertFalse(textWatermark.isSHA3256())
        assertNotNull(innamarkTag)
        assertEquals(initialInnamarkTag, innamarkTag)
    }

    @Test
    fun fromInnamarkTag_CompressedSizedCRC32InnamarkTag_success() {
        // Arrange
        val initialInnamarkTag = CompressedSizedCRC32InnamarkTag.new(textBytes)

        // Act
        val textWatermarkResult = TextWatermark.fromInnamarkTag(initialInnamarkTag)
        val innamarkTag = textWatermarkResult.value?.finish()

        // Assert
        assertTrue(textWatermarkResult.isSuccess)
        val textWatermark = textWatermarkResult.value!!
        assertEquals(text, textWatermark.text)
        assertTrue(textWatermark.isSized())
        assertTrue(textWatermark.isCompressed())
        assertTrue(textWatermark.isCRC32())
        assertFalse(textWatermark.isSHA3256())
        assertNotNull(innamarkTag)
        assertEquals(initialInnamarkTag, innamarkTag)
    }

    @Test
    fun fromInnamarkTag_SHA3256InnamarkTag_success() {
        // Arrange
        val initialInnamarkTag = SHA3256InnamarkTag.new(textBytes)

        // Act
        val textWatermarkResult = TextWatermark.fromInnamarkTag(initialInnamarkTag)
        val innamarkTag = textWatermarkResult.value?.finish()

        // Assert
        assertTrue(textWatermarkResult.isSuccess)
        val textWatermark = textWatermarkResult.value!!
        assertEquals(text, textWatermark.text)
        assertFalse(textWatermark.isSized())
        assertFalse(textWatermark.isCompressed())
        assertFalse(textWatermark.isCRC32())
        assertTrue(textWatermark.isSHA3256())
        assertNotNull(innamarkTag)
        assertEquals(initialInnamarkTag, innamarkTag)
    }

    @Test
    fun fromInnamarkTag_SizedSHA3256InnamarkTag_success() {
        // Arrange
        val initialInnamarkTag = SizedSHA3256InnamarkTag.new(textBytes)

        // Act
        val textWatermarkResult = TextWatermark.fromInnamarkTag(initialInnamarkTag)
        val innamarkTag = textWatermarkResult.value?.finish()

        // Assert
        assertTrue(textWatermarkResult.isSuccess)
        val textWatermark = textWatermarkResult.value!!
        assertEquals(text, textWatermark.text)
        assertTrue(textWatermark.isSized())
        assertFalse(textWatermark.isCompressed())
        assertFalse(textWatermark.isCRC32())
        assertTrue(textWatermark.isSHA3256())
        assertNotNull(innamarkTag)
        assertEquals(initialInnamarkTag, innamarkTag)
    }

    @Test
    fun fromInnamarkTag_CompressedSHA3256InnamarkTag_success() {
        // Arrange
        val initialInnamarkTag = CompressedSHA3256InnamarkTag.new(textBytes)

        // Act
        val textWatermarkResult = TextWatermark.fromInnamarkTag(initialInnamarkTag)
        val innamarkTag = textWatermarkResult.value?.finish()

        // Assert
        assertTrue(textWatermarkResult.isSuccess)
        val textWatermark = textWatermarkResult.value!!
        assertEquals(text, textWatermark.text)
        assertFalse(textWatermark.isSized())
        assertTrue(textWatermark.isCompressed())
        assertFalse(textWatermark.isCRC32())
        assertTrue(textWatermark.isSHA3256())
        assertNotNull(innamarkTag)
        assertEquals(initialInnamarkTag, innamarkTag)
    }

    @Test
    fun fromInnamarkTag_CompressedSizedSHA3256InnamarkTag_success() {
        // Arrange
        val initialInnamarkTag = CompressedSizedSHA3256InnamarkTag.new(textBytes)

        // Act
        val textWatermarkResult = TextWatermark.fromInnamarkTag(initialInnamarkTag)
        val innamarkTag = textWatermarkResult.value?.finish()

        // Assert
        assertTrue(textWatermarkResult.isSuccess)
        val textWatermark = textWatermarkResult.value!!
        assertEquals(text, textWatermark.text)
        assertTrue(textWatermark.isSized())
        assertTrue(textWatermark.isCompressed())
        assertFalse(textWatermark.isCRC32())
        assertTrue(textWatermark.isSHA3256())
        assertNotNull(innamarkTag)
        assertEquals(initialInnamarkTag, innamarkTag)
    }

    @Test
    fun compressed_true_true() {
        // Arrange
        val textWatermark = TextWatermark.compressed(text)

        // Act
        textWatermark.compressed()

        // Assert
        assertTrue(textWatermark.isCompressed())
    }

    @Test
    fun sized_true_true() {
        // Arrange
        val textWatermark = TextWatermark.sized(text)

        // Act
        textWatermark.sized()

        // Assert
        assertTrue(textWatermark.isSized())
    }

    @Test
    @Suppress("ktlint:standard:function-naming")
    fun CRC32_true_true() {
        // Arrange
        val textWatermark = TextWatermark.CRC32(text)

        // Act
        textWatermark.CRC32()

        // Assert
        assertTrue(textWatermark.isCRC32())
    }

    @Test
    @Suppress("ktlint:standard:function-naming")
    fun SHA3256_true_true() {
        // Arrange
        val textWatermark = TextWatermark.SHA3256(text)

        // Act
        textWatermark.SHA3256()

        // Assert
        assertTrue(textWatermark.isSHA3256())
    }
}
