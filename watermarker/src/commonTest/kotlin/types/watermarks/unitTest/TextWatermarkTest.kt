/*
 * Copyright (c) 2024-2025 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.
 *
 * This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 * that can be found in the LICENSE file.
 */

package de.fraunhofer.isst.innamark.watermarker.types.watermarks.unitTest

import de.fraunhofer.isst.innamark.watermarker.types.watermarks.CRC32InnamarkTag
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.CompressedCRC32InnamarkTag
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.CompressedRawInnamarkTag
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.CompressedSHA3256InnamarkTag
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.CompressedSizedCRC32InnamarkTag
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.CompressedSizedInnamarkTag
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.CompressedSizedSHA3256InnamarkTag
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.InnamarkTagBuilder
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.RawInnamarkTag
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.SHA3256InnamarkTag
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.SizedCRC32InnamarkTag
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.SizedInnamarkTag
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.SizedSHA3256InnamarkTag
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
        val innamarkTagBuilder = InnamarkTagBuilder.new(text)
        val innamarkTag = innamarkTagBuilder.finish()
        val content = innamarkTag.getContent()

        // Assert
        assertEquals(text, innamarkTagBuilder.text)
        assertFalse(innamarkTagBuilder.isSized())
        assertFalse(innamarkTagBuilder.isCompressed())
        assertFalse(innamarkTagBuilder.isCRC32())
        assertFalse(innamarkTagBuilder.isSHA3256())
        assertEquals(expectedInnamarkTag, innamarkTag)
        assertTrue(content.isSuccess)
        assertContentEquals(textBytes, content.value)
    }

    @Test
    fun raw_loremIpsum_success() {
        // Arrange
        val expectedInnamarkTag = RawInnamarkTag.new(textBytes)

        // Act
        val innamarkTagBuilder = InnamarkTagBuilder.raw(text)
        val innamarkTag = innamarkTagBuilder.finish()
        val content = innamarkTag.getContent()

        // Assert
        assertEquals(text, innamarkTagBuilder.text)
        assertFalse(innamarkTagBuilder.isSized())
        assertFalse(innamarkTagBuilder.isCompressed())
        assertFalse(innamarkTagBuilder.isCRC32())
        assertFalse(innamarkTagBuilder.isSHA3256())
        assertEquals(expectedInnamarkTag, innamarkTag)
        assertTrue(content.isSuccess)
        assertContentEquals(textBytes, content.value)
    }

    @Test
    fun compressed_loremIpsum_success() {
        // Arrange
        val expectedInnamarkTag = CompressedRawInnamarkTag.new(textBytes)

        // Act
        val innamarkTagBuilder = InnamarkTagBuilder.compressed(text)
        val innamarkTag = innamarkTagBuilder.finish()
        val content = innamarkTag.getContent()

        // Assert
        assertEquals(text, innamarkTagBuilder.text)
        assertFalse(innamarkTagBuilder.isSized())
        assertTrue(innamarkTagBuilder.isCompressed())
        assertFalse(innamarkTagBuilder.isCRC32())
        assertFalse(innamarkTagBuilder.isSHA3256())
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
        val innamarkTagBuilder = InnamarkTagBuilder.small(customText)
        val innamarkTag = innamarkTagBuilder.finish()
        val content = innamarkTag.getContent()

        // Assert
        assertEquals(customText, innamarkTagBuilder.text)
        assertFalse(innamarkTagBuilder.isSized())
        assertTrue(innamarkTagBuilder.isCompressed())
        assertFalse(innamarkTagBuilder.isCRC32())
        assertFalse(innamarkTagBuilder.isSHA3256())
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
        val innamarkTagBuilder = InnamarkTagBuilder.small(customText)
        val innamarkTag = innamarkTagBuilder.finish()
        val content = innamarkTag.getContent()

        // Assert
        assertEquals(customText, innamarkTagBuilder.text)
        assertFalse(innamarkTagBuilder.isSized())
        assertFalse(innamarkTagBuilder.isCompressed())
        assertFalse(innamarkTagBuilder.isCRC32())
        assertFalse(innamarkTagBuilder.isSHA3256())
        assertEquals(expectedInnamarkTag, innamarkTag)
        assertTrue(content.isSuccess)
        assertContentEquals(customTextBytes, content.value)
    }

    @Test
    fun sized_loremIpsum_success() {
        // Arrange
        val expectedInnamarkTag = SizedInnamarkTag.new(textBytes)

        // Act
        val innamarkTagBuilder = InnamarkTagBuilder.sized(text)
        val innamarkTag = innamarkTagBuilder.finish()
        val content = innamarkTag.getContent()

        // Assert
        assertEquals(text, innamarkTagBuilder.text)
        assertTrue(innamarkTagBuilder.isSized())
        assertFalse(innamarkTagBuilder.isCompressed())
        assertFalse(innamarkTagBuilder.isCRC32())
        assertFalse(innamarkTagBuilder.isSHA3256())
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
        val innamarkTagBuilder = InnamarkTagBuilder.CRC32(text)
        val innamarkTag = innamarkTagBuilder.finish()
        val content = innamarkTag.getContent()

        // Assert
        assertEquals(text, innamarkTagBuilder.text)
        assertFalse(innamarkTagBuilder.isSized())
        assertFalse(innamarkTagBuilder.isCompressed())
        assertTrue(innamarkTagBuilder.isCRC32())
        assertFalse(innamarkTagBuilder.isSHA3256())
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
        val innamarkTagBuilder = InnamarkTagBuilder.SHA3256(text)
        val innamarkTag = innamarkTagBuilder.finish()
        val content = innamarkTag.getContent()

        // Assert
        assertEquals(text, innamarkTagBuilder.text)
        assertFalse(innamarkTagBuilder.isSized())
        assertFalse(innamarkTagBuilder.isCompressed())
        assertFalse(innamarkTagBuilder.isCRC32())
        assertTrue(innamarkTagBuilder.isSHA3256())
        assertEquals(expectedInnamarkTag, innamarkTag)
        assertTrue(content.isSuccess)
        assertContentEquals(textBytes, content.value)
    }

    @Test
    fun compressedSized_loremIpsum_success() {
        // Arrange
        val expectedInnamarkTag = CompressedSizedInnamarkTag.new(textBytes)

        // Act
        val innamarkTagBuilder = InnamarkTagBuilder.compressedSized(text)
        val innamarkTag = innamarkTagBuilder.finish()
        val content = innamarkTag.getContent()

        // Assert
        assertEquals(text, innamarkTagBuilder.text)
        assertTrue(innamarkTagBuilder.isSized())
        assertTrue(innamarkTagBuilder.isCompressed())
        assertFalse(innamarkTagBuilder.isCRC32())
        assertFalse(innamarkTagBuilder.isSHA3256())
        assertEquals(expectedInnamarkTag, innamarkTag)
        assertTrue(content.isSuccess)
        assertContentEquals(textBytes, content.value)
    }

    @Test
    fun compressedCRC32_loremIpsum_success() {
        // Arrange
        val expectedInnamarkTag = CompressedCRC32InnamarkTag.new(textBytes)

        // Act
        val innamarkTagBuilder = InnamarkTagBuilder.compressedCRC32(text)
        val innamarkTag = innamarkTagBuilder.finish()
        val content = innamarkTag.getContent()

        // Assert
        assertEquals(text, innamarkTagBuilder.text)
        assertFalse(innamarkTagBuilder.isSized())
        assertTrue(innamarkTagBuilder.isCompressed())
        assertTrue(innamarkTagBuilder.isCRC32())
        assertFalse(innamarkTagBuilder.isSHA3256())
        assertEquals(expectedInnamarkTag, innamarkTag)
        assertTrue(content.isSuccess)
        assertContentEquals(textBytes, content.value)
    }

    @Test
    fun compressedSHA3256_loremIpsum_success() {
        // Arrange
        val expectedInnamarkTag = CompressedSHA3256InnamarkTag.new(textBytes)

        // Act
        val innamarkTagBuilder = InnamarkTagBuilder.compressedSHA3256(text)
        val innamarkTag = innamarkTagBuilder.finish()
        val content = innamarkTag.getContent()

        // Assert
        assertEquals(text, innamarkTagBuilder.text)
        assertFalse(innamarkTagBuilder.isSized())
        assertTrue(innamarkTagBuilder.isCompressed())
        assertFalse(innamarkTagBuilder.isCRC32())
        assertTrue(innamarkTagBuilder.isSHA3256())
        assertEquals(expectedInnamarkTag, innamarkTag)
        assertTrue(content.isSuccess)
        assertContentEquals(textBytes, content.value)
    }

    @Test
    fun sizedCRC32_loremIpsum_success() {
        // Arrange
        val expectedInnamarkTag = SizedCRC32InnamarkTag.new(textBytes)

        // Act
        val innamarkTagBuilder = InnamarkTagBuilder.sizedCRC32(text)
        val innamarkTag = innamarkTagBuilder.finish()
        val content = innamarkTag.getContent()

        // Assert
        assertEquals(text, innamarkTagBuilder.text)
        assertTrue(innamarkTagBuilder.isSized())
        assertFalse(innamarkTagBuilder.isCompressed())
        assertTrue(innamarkTagBuilder.isCRC32())
        assertFalse(innamarkTagBuilder.isSHA3256())
        assertEquals(expectedInnamarkTag, innamarkTag)
        assertTrue(content.isSuccess)
        assertContentEquals(textBytes, content.value)
    }

    @Test
    fun sizedSHA3256_loremIpsum_success() {
        // Arrange
        val expectedInnamarkTag = SizedSHA3256InnamarkTag.new(textBytes)

        // Act
        val innamarkTagBuilder = InnamarkTagBuilder.sizedSHA3256(text)
        val innamarkTag = innamarkTagBuilder.finish()
        val content = innamarkTag.getContent()

        // Assert
        assertEquals(text, innamarkTagBuilder.text)
        assertTrue(innamarkTagBuilder.isSized())
        assertFalse(innamarkTagBuilder.isCompressed())
        assertFalse(innamarkTagBuilder.isCRC32())
        assertTrue(innamarkTagBuilder.isSHA3256())
        assertEquals(expectedInnamarkTag, innamarkTag)
        assertTrue(content.isSuccess)
        assertContentEquals(textBytes, content.value)
    }

    @Test
    fun compressedSizedCRC32_loremIpsum_success() {
        // Arrange
        val expectedInnamarkTag = CompressedSizedCRC32InnamarkTag.new(textBytes)

        // Act
        val innamarkTagBuilder = InnamarkTagBuilder.compressedSizedCRC32(text)
        val innamarkTag = innamarkTagBuilder.finish()
        val content = innamarkTag.getContent()

        // Assert
        assertEquals(text, innamarkTagBuilder.text)
        assertTrue(innamarkTagBuilder.isSized())
        assertTrue(innamarkTagBuilder.isCompressed())
        assertTrue(innamarkTagBuilder.isCRC32())
        assertFalse(innamarkTagBuilder.isSHA3256())
        assertEquals(expectedInnamarkTag, innamarkTag)
        assertTrue(content.isSuccess)
        assertContentEquals(textBytes, content.value)
    }

    @Test
    fun compressedSizedSHA3256_loremIpsum_success() {
        // Arrange
        val expectedInnamarkTag = CompressedSizedSHA3256InnamarkTag.new(textBytes)

        // Act
        val innamarkTagBuilder = InnamarkTagBuilder.compressedSizedSHA3256(text)
        val innamarkTag = innamarkTagBuilder.finish()
        val content = innamarkTag.getContent()

        // Assert
        assertEquals(text, innamarkTagBuilder.text)
        assertTrue(innamarkTagBuilder.isSized())
        assertTrue(innamarkTagBuilder.isCompressed())
        assertFalse(innamarkTagBuilder.isCRC32())
        assertTrue(innamarkTagBuilder.isSHA3256())
        assertEquals(expectedInnamarkTag, innamarkTag)
        assertTrue(content.isSuccess)
        assertContentEquals(textBytes, content.value)
    }

    @Test
    fun fromInnamarkTag_RawInnamarkTag_success() {
        // Arrange
        val initialInnamarkTag = RawInnamarkTag.new(textBytes)

        // Act
        val innamarkTagBuilderResult = InnamarkTagBuilder.fromInnamarkTag(initialInnamarkTag)
        val innamarkTag = innamarkTagBuilderResult.value?.finish()

        // Assert
        assertTrue(innamarkTagBuilderResult.isSuccess)
        val textWatermark = innamarkTagBuilderResult.value!!
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
        val innamarkTagBuilderResult = InnamarkTagBuilder.fromInnamarkTag(initialInnamarkTag)
        val innamarkTag = innamarkTagBuilderResult.value?.finish()

        // Assert
        assertTrue(innamarkTagBuilderResult.isSuccess)
        val textWatermark = innamarkTagBuilderResult.value!!
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
        val innamarkTagBuilderResult = InnamarkTagBuilder.fromInnamarkTag(initialInnamarkTag)
        val innamarkTag = innamarkTagBuilderResult.value?.finish()

        // Assert
        assertTrue(innamarkTagBuilderResult.isSuccess)
        val textWatermark = innamarkTagBuilderResult.value!!
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
        val innamarkTagBuilderResult = InnamarkTagBuilder.fromInnamarkTag(initialInnamarkTag)
        val innamarkTag = innamarkTagBuilderResult.value?.finish()

        // Assert
        assertTrue(innamarkTagBuilderResult.isSuccess)
        val textWatermark = innamarkTagBuilderResult.value!!
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
        val innamarkTagBuilderResult = InnamarkTagBuilder.fromInnamarkTag(initialInnamarkTag)
        val innamarkTag = innamarkTagBuilderResult.value?.finish()

        // Assert
        assertTrue(innamarkTagBuilderResult.isSuccess)
        val textWatermark = innamarkTagBuilderResult.value!!
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
        val innamarkTagBuilderResult = InnamarkTagBuilder.fromInnamarkTag(initialInnamarkTag)
        val innamarkTag = innamarkTagBuilderResult.value?.finish()

        // Assert
        assertTrue(innamarkTagBuilderResult.isSuccess)
        val textWatermark = innamarkTagBuilderResult.value!!
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
        val innamarkTagBuilderResult = InnamarkTagBuilder.fromInnamarkTag(initialInnamarkTag)
        val innamarkTag = innamarkTagBuilderResult.value?.finish()

        // Assert
        assertTrue(innamarkTagBuilderResult.isSuccess)
        val textWatermark = innamarkTagBuilderResult.value!!
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
        val innamarkTagBuilderResult = InnamarkTagBuilder.fromInnamarkTag(initialInnamarkTag)
        val innamarkTag = innamarkTagBuilderResult.value?.finish()

        // Assert
        assertTrue(innamarkTagBuilderResult.isSuccess)
        val textWatermark = innamarkTagBuilderResult.value!!
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
        val innamarkTagBuilderResult = InnamarkTagBuilder.fromInnamarkTag(initialInnamarkTag)
        val innamarkTag = innamarkTagBuilderResult.value?.finish()

        // Assert
        assertTrue(innamarkTagBuilderResult.isSuccess)
        val textWatermark = innamarkTagBuilderResult.value!!
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
        val innamarkTagBuilderResult = InnamarkTagBuilder.fromInnamarkTag(initialInnamarkTag)
        val innamarkTag = innamarkTagBuilderResult.value?.finish()

        // Assert
        assertTrue(innamarkTagBuilderResult.isSuccess)
        val textWatermark = innamarkTagBuilderResult.value!!
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
        val innamarkTagBuilderResult = InnamarkTagBuilder.fromInnamarkTag(initialInnamarkTag)
        val innamarkTag = innamarkTagBuilderResult.value?.finish()

        // Assert
        assertTrue(innamarkTagBuilderResult.isSuccess)
        val textWatermark = innamarkTagBuilderResult.value!!
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
        val innamarkTagBuilder = InnamarkTagBuilder.compressed(text)

        // Act
        innamarkTagBuilder.compressed()

        // Assert
        assertTrue(innamarkTagBuilder.isCompressed())
    }

    @Test
    fun sized_true_true() {
        // Arrange
        val innamarkTagBuilder = InnamarkTagBuilder.sized(text)

        // Act
        innamarkTagBuilder.sized()

        // Assert
        assertTrue(innamarkTagBuilder.isSized())
    }

    @Test
    @Suppress("ktlint:standard:function-naming")
    fun CRC32_true_true() {
        // Arrange
        val innamarkTagBuilder = InnamarkTagBuilder.CRC32(text)

        // Act
        innamarkTagBuilder.CRC32()

        // Assert
        assertTrue(innamarkTagBuilder.isCRC32())
    }

    @Test
    @Suppress("ktlint:standard:function-naming")
    fun SHA3256_true_true() {
        // Arrange
        val innamarkTagBuilder = InnamarkTagBuilder.SHA3256(text)

        // Act
        innamarkTagBuilder.SHA3256()

        // Assert
        assertTrue(innamarkTagBuilder.isSHA3256())
    }
}
