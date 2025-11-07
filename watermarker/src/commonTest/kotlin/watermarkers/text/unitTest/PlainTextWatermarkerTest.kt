/*
 * Copyright (c) 2025 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.
 *
 * This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 * that can be found in the LICENSE file.
 */

package de.fraunhofer.isst.innamark.watermarker.watermarkers.text.unitTest

import de.fraunhofer.isst.innamark.watermarker.types.watermarks.Watermark
import de.fraunhofer.isst.innamark.watermarker.watermarkers.text.DefaultTranscoding
import de.fraunhofer.isst.innamark.watermarker.watermarkers.text.PlainTextWatermarker
import de.fraunhofer.isst.innamark.watermarker.watermarkers.text.PlainTextWatermarker.AlphabetContainsSeparatorError
import de.fraunhofer.isst.innamark.watermarker.watermarkers.text.PlainTextWatermarker.ContainsAlphabetCharsError
import de.fraunhofer.isst.innamark.watermarker.watermarkers.text.PlainTextWatermarker.IncompleteWatermarkWarning
import de.fraunhofer.isst.innamark.watermarker.watermarkers.text.PlainTextWatermarker.OversizedWatermarkWarning
import de.fraunhofer.isst.innamark.watermarker.watermarkers.text.SeparatorStrategy
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DefaultTranscodingTest {
    private val loremIpsumBytes =
        byteArrayOf(
            0x00, 0x01, 0x0F, 0x41, 0x62, 0xAA.toByte(), 0xF0.toByte(), 0xFE.toByte(),
            0xFF.toByte(), 0x42, 0xef.toByte(), 0xfc.toByte(), 0x2d, 0xe3.toByte(), 0xc9.toByte(),
            0xfa.toByte(), 0x08, 0x87.toByte(), 0x2d,
        )
    private val loremIpsumEncoded =
        listOf<Char>(
            '\u2008', '\u2008', '\u2008', '\u2008', '\u2009', '\u2008', '\u2008', '\u2008',
            '\u205F', '\u205F', '\u2008', '\u2008', '\u2009', '\u2008', '\u2008', '\u2009',
            '\u202F', '\u2008', '\u202F', '\u2009', '\u202F', '\u202F', '\u202F', '\u202F',
            '\u2008', '\u2008', '\u205F', '\u205F', '\u202F', '\u205F', '\u205F', '\u205F',
            '\u205F', '\u205F', '\u205F', '\u205F', '\u202F', '\u2008', '\u2008', '\u2009',
            '\u205F', '\u205F', '\u202F', '\u205F', '\u2008', '\u205F', '\u205F', '\u205F',
            '\u2009', '\u205F', '\u202F', '\u2008', '\u205F', '\u2008', '\u202F', '\u205F',
            '\u2009', '\u202F', '\u2008', '\u205F', '\u202F', '\u202F', '\u205F', '\u205F',
            '\u2008', '\u202F', '\u2008', '\u2008', '\u205F', '\u2009', '\u2008', '\u202F',
            '\u2009', '\u205F', '\u202F', '\u2008',
        )

    @Test
    fun encode_loremIpsum_success() {
        // Act
        val result = DefaultTranscoding.encode(loremIpsumBytes)

        // Assert
        assertEquals(loremIpsumEncoded, result.toList())
    }

    @Test
    fun decode_loremIpsum_success() {
        // Act
        val result = DefaultTranscoding.decode(loremIpsumEncoded.asSequence())

        // Assert
        assertTrue(result.isSuccess)
        assertContentEquals(loremIpsumBytes, result.value)
    }
}

class PlainTextWatermarkerTest {
    val watermarker = PlainTextWatermarker()
    val invalidWatermarker =
        PlainTextWatermarker(
            separatorStrategy =
                SeparatorStrategy
                    .SingleSeparatorChar(' '),
        )
    val loremIpsum =
        "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonu" +
            "my eirmod tempor invidunt ut labore et dolore magna aliquyam erat," +
            " sed diam voluptua. At vero eos et accusam et justo duo dolores et" +
            " ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est " +
            "Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur" +
            " sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labor" +
            "e et dolore magna aliquyam erat, sed diam voluptua. At vero eos et" +
            " accusam et justo duo dolores et ea rebum. "
    val emptyWatermarkedText =
        "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonu" +
            "my eirmod tempor invidunt ut labore et dolore magna aliquyam erat," +
            " sed diam voluptua. At vero eos et accusam et justo duo dolores et" +
            " ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est " +
            "Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur" +
            " sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labor" +
            "e et dolore magna aliquyam erat, sed diam voluptua. At vero eos et" +
            " accusam et justo duo dolores et ea rebum. "
    val emptyInnamarkedText =
        "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonu" +
            "my eirmod tempor invidunt ut labore et dolore magna aliquyam erat," +
            " sed diam voluptua. At vero eos et accusam et justo duo dolores et" +
            " ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est " +
            "Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur" +
            " sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labor" +
            "e et dolore magna aliquyam erat, sed diam voluptua. At vero eos et" +
            " accusam et justo duo dolores et ea rebum. "
    val watermarkedText =
        "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonu" +
            "my eirmod tempor invidunt ut labore et dolore magna aliquyam erat," +
            " sed diam voluptua. At vero eos et accusam et justo duo dolores et" +
            " ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est " +
            "Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur" +
            " sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labor" +
            "e et dolore magna aliquyam erat, sed diam voluptua. At vero eos et" +
            " accusam et justo duo dolores et ea rebum. "
    val innamarkedText =
        "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonu" +
            "my eirmod tempor invidunt ut labore et dolore magna aliquyam erat," +
            " sed diam voluptua. At vero eos et accusam et justo duo dolores et" +
            " ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est " +
            "Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur" +
            " sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labor" +
            "e et dolore magna aliquyam erat, sed diam voluptua. At vero eos et" +
            " accusam et justo duo dolores et ea rebum. "
    val watermarkedText2 =
        "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonu" +
            "my eirmod tempor invidunt ut labore et dolore magna aliquyam erat," +
            " sed diam voluptua. At vero eos et accusam et justo duo dolores et" +
            " ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est " +
            "Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur" +
            " sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labor" +
            "e et dolore magna aliquyam erat, sed diam voluptua. At vero eos et" +
            " accusam et justo duo dolores et ea rebum. "
    val combinedTextEqual = watermarkedText + watermarkedText2
    val combinedTextUnequal = watermarkedText + watermarkedText2 + watermarkedText2
    val watermark = "Test"
    val watermarkBytes = byteArrayOf(0x54.toByte(), 0x65.toByte(), 0x73.toByte(), 0x74.toByte())
    val watermark2 = "Okay"
    val watermarkBytes2 = byteArrayOf(0x4F.toByte(), 0x6B.toByte(), 0x61.toByte(), 0x79.toByte())
    val malformedBytes = byteArrayOf(0x54.toByte(), 0x65.toByte(), 0x73.toByte(), 0xFF.toByte())

    @Test
    fun placement_loremIpsum_success() {
        // Arrange
        val text =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor " +
                "incididunt ut labore et dolore magna aliqua. Blandit volutpat maecenas " +
                "volutpat blandit aliquam etiam erat velit."
        val expected =
            sequenceOf(
                5, 11, 17, 21, 27, 39, 50, 56, 60, 63, 71, 78, 89, 92, 99, 102, 109, 115, 123, 131,
                140, 149, 158, 166, 174, 180, 185,
            ).toList()

        // Act
        val result = watermarker.placement(text).toList()

        // Assert
        assertEquals(expected, result)
    }

    @Test
    fun placement_empty_success() {
        // Arrange
        val text = ""
        val expected = sequenceOf<Int>().toList()

        // Act
        val result = watermarker.placement(text).toList()

        // Assert
        assertEquals(expected, result)
    }

    @Test
    fun placement_noSpace_success() {
        // Arrange
        val text =
            "Loremdipsumdolorsitamet,consecteturadipiscingelit,seddoeiusmodtemporincididuntutlabo" +
                "reetdoloremagnaaliqua.Blanditvolutpatmaecenasvolutpatblanditaliquametiameratvelit."
        val expected = sequenceOf<Int>().toList()

        // Act
        val result = watermarker.placement(text).toList()

        // Assert
        assertEquals(expected, result)
    }

    @Test
    fun addWatermark_string_success() {
        // Arrange
        val expected = innamarkedText

        // Act
        val result = watermarker.addWatermark(loremIpsum, watermark)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expected, result.value)
    }

    @Test
    fun addWatermark_byteArray_successAndWrap() {
        // Arrange
        val expected = innamarkedText

        // Act
        val result = watermarker.addWatermark(loremIpsum, watermarkBytes, true)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expected, result.value)
    }

    @Test
    fun addWatermark_byteArray_successNoWrap() {
        // Arrange
        val expected = watermarkedText

        // Act
        val result = watermarker.addWatermark(loremIpsum, watermarkBytes, false)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expected, result.value)
    }

    @Test
    fun addWatermark_object_success() {
        // Arrange
        val expected = watermarkedText

        // Act
        val result = watermarker.addWatermark(loremIpsum, Watermark.fromString(watermark))

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expected, result.value)
    }

    @Test
    fun addWatermark_emptyString_success() {
        // Arrange
        val expected = emptyInnamarkedText

        // Act
        val result = watermarker.addWatermark(loremIpsum, "")

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expected, result.value)
    }

    @Test
    fun addWatermark_emptyCoverAndString_oversizeWarning() {
        // Arrange
        val expected = ""
        val expectedWarning = OversizedWatermarkWarning(6, 0)

        // Act
        val result = watermarker.addWatermark("", "")

        // Assert
        assertTrue(result.isWarning)
        assertEquals(expected, result.value)
        assertEquals(expectedWarning.getMessage(), result.status.getEvents()[0].getMessage())
    }

    @Test
    fun addWatermark_oversized_oversizeWarning() {
        // Arrange
        val shortText = "Lorem ipsum dolor sit amet"
        val expected = "Lorem ipsum dolor sit amet"
        val expectedWarning = OversizedWatermarkWarning(22, 4)

        // Act
        val result = watermarker.addWatermark(shortText, watermark)

        // Assert
        assertTrue(result.isWarning)
        assertEquals(expected, result.value)
        assertEquals(expectedWarning.getMessage(), result.status.getEvents()[0].getMessage())
    }

    @Test
    fun addWatermark_markedCover_AlphabetError() {
        // Arrange
        val expected = watermarkedText
        val expectedError = ContainsAlphabetCharsError(listOf(' ', ' ', ' ', ' ', ' ').asSequence())

        // Act
        val result = watermarker.addWatermark(watermarkedText, watermark)

        // Assert
        assertTrue(result.isError)
        assertEquals(expected, result.value)
        assertEquals(expectedError.getMessage(), result.status.getEvents()[0].getMessage())
    }

    @Test
    fun addWatermark_invalidWatermarker_separatorError() {
        // Arrange
        val expected = loremIpsum
        val expectedError = AlphabetContainsSeparatorError(listOf(' '))

        // Act
        val result = invalidWatermarker.addWatermark(loremIpsum, watermark)

        // Assert
        assertTrue(result.isError)
        assertEquals(expected, result.value)
        assertEquals(expectedError.getMessage(), result.status.getEvents()[0].getMessage())
    }

    @Test
    fun containsWatermark_unmarked_false() {
        // Act
        val result = watermarker.containsWatermark(loremIpsum)

        // Assert
        assertFalse(result)
    }

    @Test
    fun containsWatermark_marked_true() {
        // Act
        val result = watermarker.containsWatermark(watermarkedText)

        // Assert
        assertTrue(result)
    }

    @Test
    fun getWatermarkAsString_unmarkedText_Success() {
        // Arrange
        val expected = ""

        // Act
        val result = watermarker.getWatermarkAsString(loremIpsum)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expected, result.value)
    }

    @Test
    fun getWatermarkAsString_singleWatermark_Success() {
        // Arrange
        val expected = watermark

        // Act
        val result = watermarker.getWatermarkAsString(watermarkedText)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expected, result.value)
    }

    @Test
    fun getWatermarkAsString_multipleWatermarks_equalAndWarning() {
        // Arrange
        val expected = watermark
        val expectedStatus = Watermark.MultipleMostFrequentWarning(2)

        // Act
        val result = watermarker.getWatermarkAsString(combinedTextEqual)

        // Assert
        assertTrue(result.isWarning)
        assertEquals(result.status.getMessage(), expectedStatus.getMessage())
        assertEquals(expected, result.value)
    }

    @Test
    fun getWatermarkAsString_multipleWatermarks_unequalAndSuccess() {
        // Arrange
        val expected = watermark2

        // Act
        val result = watermarker.getWatermarkAsString(combinedTextUnequal)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expected, result.value)
    }

    @Test
    fun getWatermarkAsString_singleWatermark_StringDecodeWarning() {
        // Arrange
        val expected = "Tes" + '\uFFFD'
        val expectedStatus = Watermark.StringDecodeWarning("PlainTextWatermarker")
        val watermarked = watermarker.addWatermark(loremIpsum, malformedBytes)

        // Act
        val result = watermarker.getWatermarkAsString(watermarked.value!!)

        // Assert
        assertTrue(result.isWarning)
        assertEquals(expectedStatus.getMessage(), result.getMessage())
        assertEquals(expected, result.value)
    }

    @Test
    fun getWatermarkAsByteArray_unmarkedText_Success() {
        // Arrange
        val expected = byteArrayOf()

        // Act
        val result = watermarker.getWatermarkAsByteArray(loremIpsum)

        // Assert
        assertTrue(result.isSuccess)
        assertContentEquals(expected, result.value)
    }

    @Test
    fun getWatermarkAsByteArray_singleWatermark_Success() {
        // Arrange
        val expected = watermarkBytes

        // Act
        val result = watermarker.getWatermarkAsByteArray(watermarkedText)

        // Assert
        assertTrue(result.isSuccess)
        assertContentEquals(expected, result.value)
    }

    @Test
    fun getWatermarkAsByteArray_multipleWatermarks_equalAndWarning() {
        // Arrange
        val expected = watermarkBytes
        val expectedStatus = Watermark.MultipleMostFrequentWarning(2)

        // Act
        val result = watermarker.getWatermarkAsByteArray(combinedTextEqual)

        // Assert
        assertTrue(result.isWarning)
        assertEquals(result.status.getMessage(), expectedStatus.getMessage())
        assertContentEquals(expected, result.value)
    }

    @Test
    fun getWatermarkAsByteArray_multipleWatermarks_unequalAndSuccess() {
        // Arrange
        val expected = watermarkBytes2

        // Act
        val result = watermarker.getWatermarkAsByteArray(combinedTextUnequal)

        // Assert
        assertTrue(result.isSuccess)
        assertContentEquals(expected, result.value)
    }

    @Test
    fun getWatermarks_singleWatermark_Success() {
        // Arrange
        val expectedWatermark = Watermark.fromString(watermark2)
        val expected =
            listOf(
                expectedWatermark, expectedWatermark, expectedWatermark,
                expectedWatermark, expectedWatermark, expectedWatermark, expectedWatermark,
                expectedWatermark, expectedWatermark, expectedWatermark,
            )

        // Act
        val result =
            watermarker.getWatermarks(
                combinedTextUnequal,
                squash = false,
                singleWatermark = true,
            )

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expected, result.value)
    }

    @Test
    fun getWatermarks_singleWatermark_SuccessAndSquash() {
        // Arrange
        val expectedWatermark = Watermark.fromString(watermark2)
        val expected = listOf(expectedWatermark)

        // Act
        val result =
            watermarker.getWatermarks(
                combinedTextUnequal,
                squash = true,
                singleWatermark = true,
            )

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expected, result.value)
    }

    @Test
    fun getWatermarks_multipleWatermark_Success() {
        // Arrange
        val expectedWatermark1 = Watermark.fromString(watermark)
        val expectedWatermark2 = Watermark.fromString(watermark2)
        val expected =
            listOf(
                expectedWatermark1, expectedWatermark1, expectedWatermark1,
                expectedWatermark1, expectedWatermark1, expectedWatermark2, expectedWatermark2,
                expectedWatermark2, expectedWatermark2, expectedWatermark2,
            )

        // Act
        val result =
            watermarker.getWatermarks(
                combinedTextEqual,
                squash = false,
                singleWatermark = false,
            )

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expected, result.value)
    }

    @Test
    fun getWatermarks_multipleWatermark_SuccessAndSquash() {
        // Arrange
        val expectedWatermark1 = Watermark.fromString(watermark)
        val expectedWatermark2 = Watermark.fromString(watermark2)
        val expected = listOf(expectedWatermark1, expectedWatermark2)

        // Act
        val result =
            watermarker.getWatermarks(
                combinedTextEqual,
                squash = true,
                singleWatermark = false,
            )

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expected, result.value)
    }

    @Test
    fun getWatermarks_incompleteWatermark_incompleteWarning() {
        // Arrange
        val cover = "Loremipsum dolor sit amet, consetetur "
        val expected = listOf(Watermark(byteArrayOf(0x54, 0x01)))
        val expectedWarning = IncompleteWatermarkWarning().getMessage()

        // Act
        val result = watermarker.getWatermarks(cover)

        // Assert
        assertTrue(result.isWarning)
        assertEquals(expected, result.value)
        assertEquals(expectedWarning, result.status.getMessage())
    }

    @Test
    fun getWatermarks_invalidWatermarker_separatorError() {
        // Arrange
        val expectedError = AlphabetContainsSeparatorError(listOf(' '))

        // Act
        val result = invalidWatermarker.getWatermarks(watermarkedText)

        // Assert
        assertTrue(result.isError)
        assertNull(result.value)
        assertEquals(expectedError.getMessage(), result.status.getEvents()[0].getMessage())
    }

    @Test
    fun getWatermarks_completeWatermark_success() {
        // Arrange
        val marked = "a b c d e f g h i j k l m n o p q r s"
        val expected = listOf(Watermark.fromString("test"))

        // Act
        val result = watermarker.getWatermarks(marked)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expected, result.value)
    }

    @Test
    fun getWatermarks_missingLeadingSeparator_incompleteWarning() {
        // Arrange
        val marked = "ab c d e f g h i j k l m n o p q r s"
        val expected = listOf(Watermark.fromString("test"))
        val expectedWarning = IncompleteWatermarkWarning().getMessage()

        // Act
        val result = watermarker.getWatermarks(marked)

        // Assert
        assertTrue(result.isWarning)
        assertEquals(expected, result.value)
        assertEquals(expectedWarning, result.status.getMessage())
    }

    @Test
    fun getWatermarks_missingTrailingSeparator_incompleteWarning() {
        // Arrange
        val marked = "a b c d e f g h i j k l m n o p q rs"
        val expected = listOf(Watermark.fromString("test"))
        val expectedWarning = IncompleteWatermarkWarning().getMessage()

        // Act
        val result = watermarker.getWatermarks(marked)

        // Assert
        assertTrue(result.isWarning)
        assertEquals(expected, result.value)
        assertEquals(expectedWarning, result.status.getMessage())
    }

    @Test
    fun getWatermarks_missingTrailingSeparator2_incompleteWarning() {
        // Arrange
        val marked = "aaa b c d e f g h i j k l m n o p q rs"
        val expected = listOf(Watermark.fromString("test"))
        val expectedWarning = IncompleteWatermarkWarning().getMessage()

        // Act
        val result = watermarker.getWatermarks(marked)

        // Assert
        assertTrue(result.isWarning)
        assertEquals(expected, result.value)
        assertEquals(expectedWarning, result.status.getMessage())
    }

    @Test
    fun getWatermarks_missingBothSeparator_incompleteWarning() {
        // Arrange
        val marked = "ab c d e f g h i j k l m n o p q rs"
        val expected = listOf(Watermark.fromString("test"))
        val expectedWarning = IncompleteWatermarkWarning().getMessage()

        // Act
        val result = watermarker.getWatermarks(marked)

        // Assert
        assertTrue(result.isWarning)
        assertEquals(expected, result.value)
        assertEquals(expectedWarning, result.status.getMessage())
    }

    @Test
    fun removeWatermarks_empty_success() {
        // Arrange
        val expected = ""

        // Act
        val result = watermarker.removeWatermarks("")

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expected, result.value)
    }

    @Test
    fun removeWatermarks_unmarked_success() {
        // Arrange
        val expected = loremIpsum

        // Act
        val result = watermarker.removeWatermarks(loremIpsum)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expected, result.value)
    }

    @Test
    fun removeWatermarks_marked_success() {
        // Arrange
        val expected = loremIpsum

        // Act
        val result = watermarker.removeWatermarks(watermarkedText)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expected, result.value)
    }

    @Test
    fun incompleteWatermarkWarning_string_success() {
        // Arrange
        val error = IncompleteWatermarkWarning()
        val expected =
            "Warning (PlainTextWatermarker.getWatermark): Could not restore a complete watermark!"

        // Act
        val result = error.toString()

        // Assert
        assertEquals(expected, result)
    }

    @Test
    fun oversizedWatermarkWarning_string_success() {
        // Arrange
        val error = OversizedWatermarkWarning(10, 20)
        val expected =
            "Warning (PlainTextWatermarker.addWatermark): Could only insert 20 of 10 bytes from " +
                "the " +
                "Watermark into the cover."

        // Act
        val result = error.toString()

        // Assert
        assertEquals(expected, result)
    }

    @Test
    fun containsAlphabetCharsError_string_success() {
        // Arrange
        val error = ContainsAlphabetCharsError(sequenceOf('a', 'b'))
        val expected =
            "Error (PlainTextWatermarker.addWatermark): The input contains characters of the " +
                "watermark" +
                " transcoding alphabet. It is only possible to add a watermark to an input that" +
                " doesn't contain any watermark. Adding another watermark would potentially make " +
                "the input unusable! Maybe the input already contains a watermark?\n" +
                "\n" +
                "Contained Chars:\n" +
                "['\\u0061','\\u0062']."

        // Act
        val result = error.toString()

        // Assert
        assertEquals(expected, result)
    }

    @Test
    fun success_stringComplete_success() {
        // Arrange
        val error = PlainTextWatermarker.Success(listOf(1, 2, 3), incomplete = false)
        val expected = "Success: Added Watermark 3 times. Positions: [1, 2, 3]."

        // Act
        val result = error.toString()

        // Assert
        assertEquals(expected, result)
    }

    @Test
    fun success_stringIncomplete_success() {
        // Arrange
        val error = PlainTextWatermarker.Success(listOf(1, 2, 3), incomplete = true)
        val expected =
            "Success: Added complete Watermark 2 times and incomplete Watermark once. " +
                "Positions: [1, 2, 3]."

        // Act
        val result = error.toString()

        // Assert
        assertEquals(expected, result)
    }
}
