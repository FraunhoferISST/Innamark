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
        val expectedInnamarktTag = RawInnamarkTag.new(textBytes)

        // Act
        val textWatermark = TextWatermark.new(text)
        val InnamarktTag = textWatermark.finish()
        val content = InnamarktTag.getContent()

        // Assert
        assertEquals(text, textWatermark.text)
        assertFalse(textWatermark.isSized())
        assertFalse(textWatermark.isCompressed())
        assertFalse(textWatermark.isCRC32())
        assertFalse(textWatermark.isSHA3256())
        assertEquals(expectedInnamarktTag, InnamarktTag)
        assertTrue(content.isSuccess)
        assertContentEquals(textBytes, content.value)
    }

    @Test
    fun raw_loremIpsum_success() {
        // Arrange
        val expectedInnamarktTag = RawInnamarkTag.new(textBytes)

        // Act
        val textWatermark = TextWatermark.raw(text)
        val InnamarktTag = textWatermark.finish()
        val content = InnamarktTag.getContent()

        // Assert
        assertEquals(text, textWatermark.text)
        assertFalse(textWatermark.isSized())
        assertFalse(textWatermark.isCompressed())
        assertFalse(textWatermark.isCRC32())
        assertFalse(textWatermark.isSHA3256())
        assertEquals(expectedInnamarktTag, InnamarktTag)
        assertTrue(content.isSuccess)
        assertContentEquals(textBytes, content.value)
    }

    @Test
    fun compressed_loremIpsum_success() {
        // Arrange
        val expectedInnamarktTag = CompressedRawInnamarkTag.new(textBytes)

        // Act
        val textWatermark = TextWatermark.compressed(text)
        val InnamarktTag = textWatermark.finish()
        val content = InnamarktTag.getContent()

        // Assert
        assertEquals(text, textWatermark.text)
        assertFalse(textWatermark.isSized())
        assertTrue(textWatermark.isCompressed())
        assertFalse(textWatermark.isCRC32())
        assertFalse(textWatermark.isSHA3256())
        assertEquals(expectedInnamarktTag, InnamarktTag)
        assertTrue(content.isSuccess)
        assertContentEquals(textBytes, content.value)
    }

    @Test
    fun small_loremIpsum_compression() {
        // Arrange
        val customText = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr"
        val customTextBytes = customText.encodeToByteArray()
        val expectedInnamarktTag = CompressedRawInnamarkTag.new(customTextBytes)

        // Act
        val textWatermark = TextWatermark.small(customText)
        val InnamarktTag = textWatermark.finish()
        val content = InnamarktTag.getContent()

        // Assert
        assertEquals(customText, textWatermark.text)
        assertFalse(textWatermark.isSized())
        assertTrue(textWatermark.isCompressed())
        assertFalse(textWatermark.isCRC32())
        assertFalse(textWatermark.isSHA3256())
        assertEquals(expectedInnamarktTag, InnamarktTag)
        assertTrue(content.isSuccess)
        assertContentEquals(customTextBytes, content.value)
    }

    @Test
    fun small_loremIpsum_noCompression() {
        // Arrange
        val customText = "Lorem"
        val customTextBytes = customText.encodeToByteArray()
        val expectedInnamarktTag = RawInnamarkTag.new(customTextBytes)

        // Act
        val textWatermark = TextWatermark.small(customText)
        val InnamarktTag = textWatermark.finish()
        val content = InnamarktTag.getContent()

        // Assert
        assertEquals(customText, textWatermark.text)
        assertFalse(textWatermark.isSized())
        assertFalse(textWatermark.isCompressed())
        assertFalse(textWatermark.isCRC32())
        assertFalse(textWatermark.isSHA3256())
        assertEquals(expectedInnamarktTag, InnamarktTag)
        assertTrue(content.isSuccess)
        assertContentEquals(customTextBytes, content.value)
    }

    @Test
    fun sized_loremIpsum_success() {
        // Arrange
        val expectedInnamarktTag = SizedInnamarkTag.new(textBytes)

        // Act
        val textWatermark = TextWatermark.sized(text)
        val InnamarktTag = textWatermark.finish()
        val content = InnamarktTag.getContent()

        // Assert
        assertEquals(text, textWatermark.text)
        assertTrue(textWatermark.isSized())
        assertFalse(textWatermark.isCompressed())
        assertFalse(textWatermark.isCRC32())
        assertFalse(textWatermark.isSHA3256())
        assertEquals(expectedInnamarktTag, InnamarktTag)
        assertTrue(content.isSuccess)
        assertContentEquals(textBytes, content.value)
    }

    @Test
    @Suppress("ktlint:standard:function-naming")
    fun CRC32_loremIpsum_success() {
        // Arrange
        val expectedInnamarktTag = CRC32InnamarkTag.new(textBytes)

        // Act
        val textWatermark = TextWatermark.CRC32(text)
        val InnamarktTag = textWatermark.finish()
        val content = InnamarktTag.getContent()

        // Assert
        assertEquals(text, textWatermark.text)
        assertFalse(textWatermark.isSized())
        assertFalse(textWatermark.isCompressed())
        assertTrue(textWatermark.isCRC32())
        assertFalse(textWatermark.isSHA3256())
        assertEquals(expectedInnamarktTag, InnamarktTag)
        assertTrue(content.isSuccess)
        assertContentEquals(textBytes, content.value)
    }

    @Test
    @Suppress("ktlint:standard:function-naming")
    fun SHA3256_loremIpsum_success() {
        // Arrange
        val expectedInnamarktTag = SHA3256InnamarkTag.new(textBytes)

        // Act
        val textWatermark = TextWatermark.SHA3256(text)
        val InnamarktTag = textWatermark.finish()
        val content = InnamarktTag.getContent()

        // Assert
        assertEquals(text, textWatermark.text)
        assertFalse(textWatermark.isSized())
        assertFalse(textWatermark.isCompressed())
        assertFalse(textWatermark.isCRC32())
        assertTrue(textWatermark.isSHA3256())
        assertEquals(expectedInnamarktTag, InnamarktTag)
        assertTrue(content.isSuccess)
        assertContentEquals(textBytes, content.value)
    }

    @Test
    fun compressedSized_loremIpsum_success() {
        // Arrange
        val expectedInnamarktTag = CompressedSizedInnamarkTag.new(textBytes)

        // Act
        val textWatermark = TextWatermark.compressedSized(text)
        val InnamarktTag = textWatermark.finish()
        val content = InnamarktTag.getContent()

        // Assert
        assertEquals(text, textWatermark.text)
        assertTrue(textWatermark.isSized())
        assertTrue(textWatermark.isCompressed())
        assertFalse(textWatermark.isCRC32())
        assertFalse(textWatermark.isSHA3256())
        assertEquals(expectedInnamarktTag, InnamarktTag)
        assertTrue(content.isSuccess)
        assertContentEquals(textBytes, content.value)
    }

    @Test
    fun compressedCRC32_loremIpsum_success() {
        // Arrange
        val expectedInnamarktTag = CompressedCRC32InnamarkTag.new(textBytes)

        // Act
        val textWatermark = TextWatermark.compressedCRC32(text)
        val InnamarktTag = textWatermark.finish()
        val content = InnamarktTag.getContent()

        // Assert
        assertEquals(text, textWatermark.text)
        assertFalse(textWatermark.isSized())
        assertTrue(textWatermark.isCompressed())
        assertTrue(textWatermark.isCRC32())
        assertFalse(textWatermark.isSHA3256())
        assertEquals(expectedInnamarktTag, InnamarktTag)
        assertTrue(content.isSuccess)
        assertContentEquals(textBytes, content.value)
    }

    @Test
    fun compressedSHA3256_loremIpsum_success() {
        // Arrange
        val expectedInnamarktTag = CompressedSHA3256InnamarkTag.new(textBytes)

        // Act
        val textWatermark = TextWatermark.compressedSHA3256(text)
        val InnamarktTag = textWatermark.finish()
        val content = InnamarktTag.getContent()

        // Assert
        assertEquals(text, textWatermark.text)
        assertFalse(textWatermark.isSized())
        assertTrue(textWatermark.isCompressed())
        assertFalse(textWatermark.isCRC32())
        assertTrue(textWatermark.isSHA3256())
        assertEquals(expectedInnamarktTag, InnamarktTag)
        assertTrue(content.isSuccess)
        assertContentEquals(textBytes, content.value)
    }

    @Test
    fun sizedCRC32_loremIpsum_success() {
        // Arrange
        val expectedInnamarktTag = SizedCRC32InnamarkTag.new(textBytes)

        // Act
        val textWatermark = TextWatermark.sizedCRC32(text)
        val InnamarktTag = textWatermark.finish()
        val content = InnamarktTag.getContent()

        // Assert
        assertEquals(text, textWatermark.text)
        assertTrue(textWatermark.isSized())
        assertFalse(textWatermark.isCompressed())
        assertTrue(textWatermark.isCRC32())
        assertFalse(textWatermark.isSHA3256())
        assertEquals(expectedInnamarktTag, InnamarktTag)
        assertTrue(content.isSuccess)
        assertContentEquals(textBytes, content.value)
    }

    @Test
    fun sizedSHA3256_loremIpsum_success() {
        // Arrange
        val expectedInnamarktTag = SizedSHA3256InnamarkTag.new(textBytes)

        // Act
        val textWatermark = TextWatermark.sizedSHA3256(text)
        val InnamarktTag = textWatermark.finish()
        val content = InnamarktTag.getContent()

        // Assert
        assertEquals(text, textWatermark.text)
        assertTrue(textWatermark.isSized())
        assertFalse(textWatermark.isCompressed())
        assertFalse(textWatermark.isCRC32())
        assertTrue(textWatermark.isSHA3256())
        assertEquals(expectedInnamarktTag, InnamarktTag)
        assertTrue(content.isSuccess)
        assertContentEquals(textBytes, content.value)
    }

    @Test
    fun compressedSizedCRC32_loremIpsum_success() {
        // Arrange
        val expectedInnamarktTag = CompressedSizedCRC32InnamarkTag.new(textBytes)

        // Act
        val textWatermark = TextWatermark.compressedSizedCRC32(text)
        val InnamarktTag = textWatermark.finish()
        val content = InnamarktTag.getContent()

        // Assert
        assertEquals(text, textWatermark.text)
        assertTrue(textWatermark.isSized())
        assertTrue(textWatermark.isCompressed())
        assertTrue(textWatermark.isCRC32())
        assertFalse(textWatermark.isSHA3256())
        assertEquals(expectedInnamarktTag, InnamarktTag)
        assertTrue(content.isSuccess)
        assertContentEquals(textBytes, content.value)
    }

    @Test
    fun compressedSizedSHA3256_loremIpsum_success() {
        // Arrange
        val expectedInnamarktTag = CompressedSizedSHA3256InnamarkTag.new(textBytes)

        // Act
        val textWatermark = TextWatermark.compressedSizedSHA3256(text)
        val InnamarktTag = textWatermark.finish()
        val content = InnamarktTag.getContent()

        // Assert
        assertEquals(text, textWatermark.text)
        assertTrue(textWatermark.isSized())
        assertTrue(textWatermark.isCompressed())
        assertFalse(textWatermark.isCRC32())
        assertTrue(textWatermark.isSHA3256())
        assertEquals(expectedInnamarktTag, InnamarktTag)
        assertTrue(content.isSuccess)
        assertContentEquals(textBytes, content.value)
    }

    @Test
    fun fromInnamarktTag_RawInnamarktTag_success() {
        // Arrange
        val initialInnamarktTag = RawInnamarkTag.new(textBytes)

        // Act
        val textWatermarkResult = TextWatermark.fromInnamarkTag(initialInnamarktTag)
        val InnamarktTag = textWatermarkResult.value?.finish()

        // Assert
        assertTrue(textWatermarkResult.isSuccess)
        val textWatermark = textWatermarkResult.value!!
        assertEquals(text, textWatermark.text)
        assertFalse(textWatermark.isSized())
        assertFalse(textWatermark.isCompressed())
        assertFalse(textWatermark.isCRC32())
        assertFalse(textWatermark.isSHA3256())
        assertNotNull(InnamarktTag)
        assertEquals(initialInnamarktTag, InnamarktTag)
    }

    @Test
    fun fromInnamarktTag_SizedInnamarktTag_success() {
        // Arrange
        val initialInnamarktTag = SizedInnamarkTag.new(textBytes)

        // Act
        val textWatermarkResult = TextWatermark.fromInnamarkTag(initialInnamarktTag)
        val InnamarktTag = textWatermarkResult.value?.finish()

        // Assert
        assertTrue(textWatermarkResult.isSuccess)
        val textWatermark = textWatermarkResult.value!!
        assertEquals(text, textWatermark.text)
        assertTrue(textWatermark.isSized())
        assertFalse(textWatermark.isCompressed())
        assertFalse(textWatermark.isCRC32())
        assertFalse(textWatermark.isSHA3256())
        assertNotNull(InnamarktTag)
        assertEquals(initialInnamarktTag, InnamarktTag)
    }

    @Test
    fun fromInnamarktTag_CompressedSizedInnamarktTag_success() {
        // Arrange
        val initialInnamarktTag = CompressedSizedInnamarkTag.new(textBytes)

        // Act
        val textWatermarkResult = TextWatermark.fromInnamarkTag(initialInnamarktTag)
        val InnamarktTag = textWatermarkResult.value?.finish()

        // Assert
        assertTrue(textWatermarkResult.isSuccess)
        val textWatermark = textWatermarkResult.value!!
        assertEquals(text, textWatermark.text)
        assertTrue(textWatermark.isSized())
        assertTrue(textWatermark.isCompressed())
        assertFalse(textWatermark.isCRC32())
        assertFalse(textWatermark.isSHA3256())
        assertNotNull(InnamarktTag)
        assertEquals(initialInnamarktTag, InnamarktTag)
    }

    @Test
    fun fromInnamarktTag_CRC32InnamarktTag_success() {
        // Arrange
        val initialInnamarktTag = CRC32InnamarkTag.new(textBytes)

        // Act
        val textWatermarkResult = TextWatermark.fromInnamarkTag(initialInnamarktTag)
        val InnamarktTag = textWatermarkResult.value?.finish()

        // Assert
        assertTrue(textWatermarkResult.isSuccess)
        val textWatermark = textWatermarkResult.value!!
        assertEquals(text, textWatermark.text)
        assertFalse(textWatermark.isSized())
        assertFalse(textWatermark.isCompressed())
        assertTrue(textWatermark.isCRC32())
        assertFalse(textWatermark.isSHA3256())
        assertNotNull(InnamarktTag)
        assertEquals(initialInnamarktTag, InnamarktTag)
    }

    @Test
    fun fromInnamarktTag_SizedCRC32InnamarktTag_success() {
        // Arrange
        val initialInnamarktTag = SizedCRC32InnamarkTag.new(textBytes)

        // Act
        val textWatermarkResult = TextWatermark.fromInnamarkTag(initialInnamarktTag)
        val InnamarktTag = textWatermarkResult.value?.finish()

        // Assert
        assertTrue(textWatermarkResult.isSuccess)
        val textWatermark = textWatermarkResult.value!!
        assertEquals(text, textWatermark.text)
        assertTrue(textWatermark.isSized())
        assertFalse(textWatermark.isCompressed())
        assertTrue(textWatermark.isCRC32())
        assertFalse(textWatermark.isSHA3256())
        assertNotNull(InnamarktTag)
        assertEquals(initialInnamarktTag, InnamarktTag)
    }

    @Test
    fun fromInnamarktTag_CompressedCRC32InnamarktTag_success() {
        // Arrange
        val initialInnamarktTag = CompressedCRC32InnamarkTag.new(textBytes)

        // Act
        val textWatermarkResult = TextWatermark.fromInnamarkTag(initialInnamarktTag)
        val InnamarktTag = textWatermarkResult.value?.finish()

        // Assert
        assertTrue(textWatermarkResult.isSuccess)
        val textWatermark = textWatermarkResult.value!!
        assertEquals(text, textWatermark.text)
        assertFalse(textWatermark.isSized())
        assertTrue(textWatermark.isCompressed())
        assertTrue(textWatermark.isCRC32())
        assertFalse(textWatermark.isSHA3256())
        assertNotNull(InnamarktTag)
        assertEquals(initialInnamarktTag, InnamarktTag)
    }

    @Test
    fun fromInnamarktTag_CompressedSizedCRC32InnamarktTag_success() {
        // Arrange
        val initialInnamarktTag = CompressedSizedCRC32InnamarkTag.new(textBytes)

        // Act
        val textWatermarkResult = TextWatermark.fromInnamarkTag(initialInnamarktTag)
        val InnamarktTag = textWatermarkResult.value?.finish()

        // Assert
        assertTrue(textWatermarkResult.isSuccess)
        val textWatermark = textWatermarkResult.value!!
        assertEquals(text, textWatermark.text)
        assertTrue(textWatermark.isSized())
        assertTrue(textWatermark.isCompressed())
        assertTrue(textWatermark.isCRC32())
        assertFalse(textWatermark.isSHA3256())
        assertNotNull(InnamarktTag)
        assertEquals(initialInnamarktTag, InnamarktTag)
    }

    @Test
    fun fromInnamarktTag_SHA3256InnamarktTag_success() {
        // Arrange
        val initialInnamarktTag = SHA3256InnamarkTag.new(textBytes)

        // Act
        val textWatermarkResult = TextWatermark.fromInnamarkTag(initialInnamarktTag)
        val InnamarktTag = textWatermarkResult.value?.finish()

        // Assert
        assertTrue(textWatermarkResult.isSuccess)
        val textWatermark = textWatermarkResult.value!!
        assertEquals(text, textWatermark.text)
        assertFalse(textWatermark.isSized())
        assertFalse(textWatermark.isCompressed())
        assertFalse(textWatermark.isCRC32())
        assertTrue(textWatermark.isSHA3256())
        assertNotNull(InnamarktTag)
        assertEquals(initialInnamarktTag, InnamarktTag)
    }

    @Test
    fun fromInnamarktTag_SizedSHA3256InnamarktTag_success() {
        // Arrange
        val initialInnamarktTag = SizedSHA3256InnamarkTag.new(textBytes)

        // Act
        val textWatermarkResult = TextWatermark.fromInnamarkTag(initialInnamarktTag)
        val InnamarktTag = textWatermarkResult.value?.finish()

        // Assert
        assertTrue(textWatermarkResult.isSuccess)
        val textWatermark = textWatermarkResult.value!!
        assertEquals(text, textWatermark.text)
        assertTrue(textWatermark.isSized())
        assertFalse(textWatermark.isCompressed())
        assertFalse(textWatermark.isCRC32())
        assertTrue(textWatermark.isSHA3256())
        assertNotNull(InnamarktTag)
        assertEquals(initialInnamarktTag, InnamarktTag)
    }

    @Test
    fun fromInnamarktTag_CompressedSHA3256InnamarktTag_success() {
        // Arrange
        val initialInnamarktTag = CompressedSHA3256InnamarkTag.new(textBytes)

        // Act
        val textWatermarkResult = TextWatermark.fromInnamarkTag(initialInnamarktTag)
        val InnamarktTag = textWatermarkResult.value?.finish()

        // Assert
        assertTrue(textWatermarkResult.isSuccess)
        val textWatermark = textWatermarkResult.value!!
        assertEquals(text, textWatermark.text)
        assertFalse(textWatermark.isSized())
        assertTrue(textWatermark.isCompressed())
        assertFalse(textWatermark.isCRC32())
        assertTrue(textWatermark.isSHA3256())
        assertNotNull(InnamarktTag)
        assertEquals(initialInnamarktTag, InnamarktTag)
    }

    @Test
    fun fromInnamarktTag_CompressedSizedSHA3256InnamarktTag_success() {
        // Arrange
        val initialInnamarktTag = CompressedSizedSHA3256InnamarkTag.new(textBytes)

        // Act
        val textWatermarkResult = TextWatermark.fromInnamarkTag(initialInnamarktTag)
        val InnamarktTag = textWatermarkResult.value?.finish()

        // Assert
        assertTrue(textWatermarkResult.isSuccess)
        val textWatermark = textWatermarkResult.value!!
        assertEquals(text, textWatermark.text)
        assertTrue(textWatermark.isSized())
        assertTrue(textWatermark.isCompressed())
        assertFalse(textWatermark.isCRC32())
        assertTrue(textWatermark.isSHA3256())
        assertNotNull(InnamarktTag)
        assertEquals(initialInnamarktTag, InnamarktTag)
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
