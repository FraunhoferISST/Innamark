/*
 * Copyright (c) 2023-2025 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.
 *
 * This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 * that can be found in the LICENSE file.
 */

package de.fraunhofer.isst.innamark.watermarker.watermarkers.text

import de.fraunhofer.isst.innamark.watermarker.types.responses.Event
import de.fraunhofer.isst.innamark.watermarker.types.responses.Result
import de.fraunhofer.isst.innamark.watermarker.types.responses.Status
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.InnamarkTag
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.InnamarkTagBuilder
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.RawInnamarkTag
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.Watermark
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.Watermark.MultipleMostFrequentWarning
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.Watermark.StringDecodeWarning
import de.fraunhofer.isst.innamark.watermarker.utils.toIntUnsigned
import de.fraunhofer.isst.innamark.watermarker.utils.toUnicodeRepresentation
import kotlin.js.JsName
import kotlin.math.ceil
import kotlin.math.log
import kotlin.math.pow

/** Defines how multiple watermarks are separated */
sealed class SeparatorStrategy {
    /** Leaves one insertable position empty to mark the end of a watermark */
    object SkipInsertPosition : SeparatorStrategy()

    /** Inserts [char] as separator between watermarks */
    class SingleSeparatorChar(val char: Char) : SeparatorStrategy()

    /** Inserts [start] before a Watermark and [end] after a Watermark as separators */
    class StartEndSeparatorChars(val start: Char, val end: Char) : SeparatorStrategy()
}

interface Transcoding {
    val alphabet: List<Char>

    /** Encodes [bytes] to chars of [alphabet] */
    fun encode(bytes: ByteArray): Sequence<Char>

    /** Decodes [chars] of [alphabet] to bytes */
    fun decode(chars: Sequence<Char>): Result<ByteArray>
}

object DefaultTranscoding : Transcoding {
    override val alphabet =
        listOf(
            // Punctuation space
            '\u2008',
            // Thin space
            '\u2009',
            // Narrow-no-break space
            '\u202F',
            // Medium mathematical space
            '\u205F',
        )
    const val SEPARATOR_CHAR = '\u2004' // Three-per-em space

    private val base = alphabet.size
    private val digitsPerByte = calculateDigitsPerByte(base)
    private val digitToNumber = HashMap<Char, Int>()

    init {
        // Generate HashMap from Digit to numerical Value (i.e. index of the digit)
        for ((index, char) in alphabet.withIndex()) {
            digitToNumber[char] = index
        }
    }

    /** Encodes [bytes] to a chars of [alphabet] */
    override fun encode(bytes: ByteArray): Sequence<Char> =
        sequence {
            for (byte in bytes) {
                var iByte = byte.toIntUnsigned()
                repeat(digitsPerByte) {
                    val digit = iByte % base
                    iByte /= base

                    yield(alphabet[digit])
                }
                check(iByte == 0)
            }
        }

    /** Decodes [chars] of [alphabet] to bytes */
    override fun decode(chars: Sequence<Char>): Result<ByteArray> {
        val status = Status()
        val dBase = base.toDouble()

        val result = ArrayList<Byte>()
        for (byte in chars.chunked(digitsPerByte)) {
            var iByte = 0
            for ((index, digit) in byte.withIndex()) {
                iByte += digitToNumber[digit]!! * dBase.pow(index).toInt()
            }
            if (iByte in 0..255) {
                result.add(iByte.toByte())
            } else {
                status.addEvent(DecodingInvalidByteError(iByte))
            }
        }

        return status.into(result.toByteArray())
    }

    /** Calculates how many digits are required for a byte in given base ([alphabetSize]) */
    private fun calculateDigitsPerByte(alphabetSize: Int) =
        ceil(log(256.0, 2.0) / log(alphabetSize.toDouble(), 2.0)).toInt()

    private const val SOURCE = "DefaultTranscoding"

    class DecodingInvalidByteError(val invalidByte: Int) :
        Event.Warning("$SOURCE.decode") {
        /** Returns a String explaining the event */
        override fun getMessage(): String = "Decoding produced an invalid byte: $invalidByte"
    }
}

/**
 * Implementation of [TextWatermarker] for watermarking plaintext
 *
 * Takes optional arguments to customize behavior:
 * - [transcoding]: for defining the watermarking alphabet, encoding, and decoding.
 * - [separatorStrategy]: for defining how multiple watermarks are separated.
 * - [placement]: function for finding positions where transcoding alphabet characters are inserted.
 */
class PlainTextWatermarker(
    private val transcoding: Transcoding = DefaultTranscoding,
    private val separatorStrategy: SeparatorStrategy =
        SeparatorStrategy.SingleSeparatorChar(DefaultTranscoding.SEPARATOR_CHAR),
    val placement: (String) -> List<Int> = { string ->
        sequence {
            for ((index, char) in string.withIndex()) {
                if (char == ' ') yield(index)
            }
        }.toMutableList() // mutable for JS compatibility on empty lists
    },
) : TextWatermarker {
    // Validate if transcoding and separatorStrategy are configured correctly to not overlap chars
    private val validationStatus = Status.success()

    init {
        when (val separatorStrategy = separatorStrategy) {
            is SeparatorStrategy.SkipInsertPosition -> {}
            is SeparatorStrategy.SingleSeparatorChar -> {
                if (separatorStrategy.char in transcoding.alphabet) {
                    validationStatus.addEvent(
                        AlphabetContainsSeparatorError(listOf(separatorStrategy.char)),
                    )
                }
            }

            is SeparatorStrategy.StartEndSeparatorChars -> {
                val list = ArrayList<Char>()
                if (separatorStrategy.start in transcoding.alphabet) {
                    list.add(separatorStrategy.start)
                }
                if (separatorStrategy.end in transcoding.alphabet) {
                    list.add(separatorStrategy.end)
                }
                if (!list.isEmpty()) {
                    validationStatus.addEvent(AlphabetContainsSeparatorError(list))
                }
            }
        }
    }

    // Build a list of all chars that are contained in a watermark
    private val fullAlphabet: List<Char> =
        when (separatorStrategy) {
            is SeparatorStrategy.SkipInsertPosition -> transcoding.alphabet
            is SeparatorStrategy.SingleSeparatorChar ->
                listOf(separatorStrategy.char) + transcoding.alphabet

            is SeparatorStrategy.StartEndSeparatorChars ->
                listOf(separatorStrategy.start, separatorStrategy.end) + transcoding.alphabet
        }

    /**
     * Adds a watermark created from [watermark] ByteArray to [cover]
     *
     * Returns a [OversizedWatermarkWarning] if the watermark does not fit at least a single time into the cover.
     * Returns a [ContainsAlphabetCharsError] if the cover contains a character from the transcoding alphabet.
     * Returns an [AlphabetContainsSeparatorError] if the transcoding alphabet and Separator char(s) overlap.
     */
    override fun addWatermark(
        cover: String,
        watermark: ByteArray,
        wrap: Boolean,
    ): Result<String> {
        if (validationStatus.isError) return validationStatus.into(cover)
        if (cover.any { char -> char in fullAlphabet }) {
            val containedChars = fullAlphabet.asSequence().filter { char -> char in cover }
            return ContainsAlphabetCharsError(containedChars).into(cover)
        }

        val insertPositions = placement(cover)
        val actualWatermark =
            if (wrap) {
                RawInnamarkTag.new(watermark).watermarkContent
            } else {
                watermark
            }
        val separatedWatermark = getSeparatedWatermark(actualWatermark)

        // Insert watermark
        val positionChunks =
            when (separatorStrategy) {
                is SeparatorStrategy.SkipInsertPosition ->
                    insertPositions.chunked(separatedWatermark.count() + 1)

                else -> insertPositions.chunked(separatedWatermark.count())
            }
        val startPositions = ArrayList<Int>()

        val result = StringBuilder()
        var incomplete = false
        var lastPosition = 0
        for (positions in positionChunks) {
            when (separatorStrategy) {
                is SeparatorStrategy.SkipInsertPosition -> {
                    if (positions.size != separatedWatermark.count() + 1) {
                        incomplete = true
                    }
                }
                else -> {
                    if (positions.size != separatedWatermark.count()) {
                        incomplete = true
                    }
                }
            }
            startPositions.add(positions.first())
            for ((position, char) in positions.asSequence().zip(separatedWatermark)) {
                result.append(cover.substring(lastPosition, position))
                result.append(char)
                lastPosition = position + 1
            }
        }
        result.append(cover.substring(lastPosition, cover.length))

        // Check if watermark fits at least one time into the cover with given positioning
        if (insertPositions.count() < getMinimumInsertPositions(actualWatermark)) {
            return OversizedWatermarkWarning(
                getMinimumInsertPositions(actualWatermark),
                insertPositions.count(),
            ).into(result.toString())
        }

        return Success(startPositions, incomplete).into(result.toString())
    }

    /**
     * Adds a watermark created from [watermark] String to [cover]
     *
     * Returns a [OversizedWatermarkWarning] if the watermark does not fit at least a single time into the cover.
     * Returns a [ContainsAlphabetCharsError] if the cover contains a character from the transcoding alphabet.
     * Returns an [AlphabetContainsSeparatorError] if the transcoding alphabet and Separator char(s) overlap.
     */
    override fun addWatermark(
        cover: String,
        watermark: String,
    ): Result<String> {
        if (validationStatus.isError) return validationStatus.into(cover)
        return addWatermark(cover, watermark.encodeToByteArray())
    }

    /**
     * Adds watermark object [watermark] to [cover]
     *
     * Returns a [OversizedWatermarkWarning] if the watermark does not fit at least a single time into the cover.
     * Returns a [ContainsAlphabetCharsError] if the cover contains a character from the transcoding alphabet.
     * Returns an [AlphabetContainsSeparatorError] if the transcoding alphabet and Separator char(s) overlap.
     */
    override fun addWatermark(
        cover: String,
        watermark: Watermark,
    ): Result<String> {
        if (validationStatus.isError) return validationStatus.into(cover)
        val result = addWatermark(cover, watermark.watermarkContent, wrap = false)
        return result
    }

    /** Returns a [Boolean] indicating whether [cover] contains watermarks */
    override fun containsWatermark(cover: String): Boolean {
        return cover.any { char -> char in fullAlphabet }
    }

    /**
     * Returns a [Result] containing the most frequent Watermark in [cover] as a String
     *
     * Result contains an empty String if no Watermarks were found.
     * Result contains a [MultipleMostFrequentWarning] in cases where an unambiguous Watermark could not be extracted.
     * Result contains a [StringDecodeWarning] in cases where a byte cannot be read as UTF-8.
     * Returns an [AlphabetContainsSeparatorError] if the transcoding alphabet and Separator char(s) overlap.
     */
    override fun getWatermarkAsString(cover: String): Result<String> {
        if (validationStatus.isError) return validationStatus.into()
        val watermarks = getWatermarks(cover, false, true)
        if (watermarks.value?.isNotEmpty() ?: return Result.success("")) {
            val decoded =
                if (InnamarkTag.parse(watermarks.value[0].watermarkContent).isError) {
                    watermarks.status.into(
                        watermarks.value[0].watermarkContent
                            .decodeToString(),
                    )
                } else {
                    watermarks.status.into(
                        watermarks.value[0].watermarkContent.drop(1).toByteArray()
                            .decodeToString(),
                    )
                }
            if (decoded.value!!.contains('\uFFFD')) {
                decoded.appendStatus(Status(StringDecodeWarning("PlainTextWatermarker")))
            }
            return decoded
        } else {
            return Result.success("")
        }
    }

    /**
     * Returns a [Result] containing the most frequent Watermark in [cover] as a ByteArray
     *
     * Result contains an empty ByteArray if no Watermarks were found.
     * Result contains a [MultipleMostFrequentWarning] in cases where an unambiguous Watermark could not be extracted.
     * Returns an [AlphabetContainsSeparatorError] if the transcoding alphabet and Separator char(s) overlap.
     */
    override fun getWatermarkAsByteArray(cover: String): Result<ByteArray> {
        if (validationStatus.isError) return validationStatus.into()
        val watermarks = getWatermarks(cover, false, true)
        if (watermarks.value?.isNotEmpty() ?: return Result.success(ByteArray(0))) {
            return watermarks.status.into(watermarks.value[0].watermarkContent)
        } else {
            return Result.success(ByteArray(0))
        }
    }

    /**
     * Returns a [Result] containing a list of [Watermark]s in [cover]. Attempts to parse
     * Watermarks found into [InnamarkTag]s and returns them instead if [InnamarkTag.validate]
     * returns [Event.Success] on all Watermarks.
     *
     * When [squash] is true: watermarks with the same content are merged.
     * When [singleWatermark] is true: only the most frequent watermark is returned.
     * Returns an [IncompleteWatermarkWarning] if a complete watermark could not be found.
     * Returns an [AlphabetContainsSeparatorError] if the transcoding alphabet and Separator char(s) overlap.
     */
    override fun getWatermarks(
        cover: String,
        squash: Boolean,
        singleWatermark: Boolean,
    ): Result<List<Watermark>> {
        if (validationStatus.isError) return validationStatus.into()
        val watermarkRanges: Sequence<Pair<Int, Int>> =
            when (separatorStrategy) {
                is SeparatorStrategy.SkipInsertPosition -> {
                    val insertPositions = placement(cover)
                    val separatorPositions =
                        insertPositions.filter { position ->
                            position > 0 && cover[position - 1] !in transcoding.alphabet
                        }
                    sequence {
                        var lastSeparatorPosition = 0
                        for (separatorPosition in separatorPositions) {
                            yield(lastSeparatorPosition to separatorPosition)
                            lastSeparatorPosition = separatorPosition
                        }
                    }
                }

                is SeparatorStrategy.SingleSeparatorChar -> {
                    val separatorPositions =
                        cover.asSequence().withIndex().filter { (_, char) ->
                            char == separatorStrategy.char
                        }
                            .map { (position, _) -> position }

                    sequence {
                        if (separatorPositions.count() > 1) {
                            var lastSeparatorPosition = 0
                            for (separatorPosition in separatorPositions) {
                                if (lastSeparatorPosition != separatorPosition - 1) {
                                    yield(lastSeparatorPosition to separatorPosition - 1)
                                }
                                lastSeparatorPosition = separatorPosition + 1
                            }
                        }
                    }
                }

                is SeparatorStrategy.StartEndSeparatorChars -> {
                    sequence {
                        var lastEndPosition = 0
                        var startPosition: Int? = null
                        for ((position, char) in cover.withIndex()) {
                            when (char) {
                                separatorStrategy.start -> startPosition = position + 1
                                separatorStrategy.end -> {
                                    if (startPosition == null) {
                                        startPosition = lastEndPosition + 1
                                    }
                                    yield(startPosition to position - 1)
                                    lastEndPosition = position
                                }
                            }
                        }
                    }
                }
            }

        val sanitizedWatermarkRanges =
            if (watermarkRanges.count() <= 0) {
                sequenceOf(0 to cover.length)
            } else {
                watermarkRanges
            }

        val status = Status()
        var watermarks = ArrayList<Watermark>()
        val stringBuilder = StringBuilder(cover)
        var previousStart = 0
        for ((start, end) in sanitizedWatermarkRanges) {
            val content =
                stringBuilder
                    .deleteRange(0, start - previousStart)
                    .take(end - start + 1)
                    .filter { char -> char in transcoding.alphabet }

            if (content.isNotEmpty()) {
                val decoded =
                    with(transcoding.decode(content.asSequence())) {
                        if (!hasValue) {
                            return this.status.into()
                        }
                        status.appendStatus(this.status)
                        value!!
                    }

                watermarks.add(Watermark(decoded))
            }
            previousStart = start
        }

        if (watermarkRanges.count() <= 0 && watermarks.isNotEmpty()) {
            status.addEvent(IncompleteWatermarkWarning())
        }
        if (singleWatermark && watermarks.isNotEmpty()) {
            with(Watermark.mostFrequent(watermarks)) {
                status.appendStatus(this.status)
                watermarks = this.value as ArrayList<Watermark>
            }
        }
        if (squash && watermarks.isNotEmpty()) {
            watermarks = ArrayList(squashWatermarks(watermarks))
        }

        val innamarkTags: ArrayList<InnamarkTag> = ArrayList()
        if (watermarks.isNotEmpty()) {
            for (watermark in watermarks) {
                val parsed = InnamarkTag.fromWatermark(watermark)
                if (parsed.status.isSuccess) {
                    status.appendStatus(parsed.status)
                    if (parsed.hasValue) innamarkTags.add(parsed.value!!)
                }
            }
        }
        return if (innamarkTags.isNotEmpty()) {
            status.into(innamarkTags)
        } else {
            status.into(watermarks)
        }
    }

    /** Removes all watermarks from [cover] and returns a [Result] containing the cleaned cover */
    override fun removeWatermarks(cover: String): Result<String> {
        // Replace all chars from the file that are in the transcoding alphabet with a whitespace
        val cleaned =
            cover.asSequence().map { char ->
                if (char in fullAlphabet) ' ' else char
            }
        return Result.success(cleaned.joinToString(""))
    }

    /** Counts the minimum number of insert positions needed in a text to insert the [watermark] */
    @JsName("getMinimumInsertPositionsBytes")
    fun getMinimumInsertPositions(watermark: ByteArray): Int {
        val separatedWatermark = getSeparatedWatermark(watermark)
        return if (separatorStrategy is SeparatorStrategy.StartEndSeparatorChars) {
            separatedWatermark.count()
        } else {
            separatedWatermark.count() + 1
        }
    }

    /** Counts the minimum number of insert positions needed in a text to insert the [watermark] */
    fun getMinimumInsertPositions(watermark: Watermark): Int =
        getMinimumInsertPositions(watermark.watermarkContent)

    /**
     * Counts the minimum number of insert positions needed in a text to insert the
     * [innamarkTagBuilder]
     */
    @JsName("getMimimumInsertPositionsInnamarkTagBuilder")
    fun getMinimumInsertPositions(innamarkTagBuilder: InnamarkTagBuilder): Int =
        getMinimumInsertPositions(innamarkTagBuilder.finish())

    /** Transforms a [watermark] into a separated watermark */
    private fun getSeparatedWatermark(watermark: ByteArray): Sequence<Char> {
        val encodedWatermark = transcoding.encode(watermark)

        val separatedWatermark =
            when (separatorStrategy) {
                is SeparatorStrategy.SkipInsertPosition -> encodedWatermark
                is SeparatorStrategy.SingleSeparatorChar ->
                    sequence {
                        yield(separatorStrategy.char)
                        yieldAll(encodedWatermark)
                    }

                is SeparatorStrategy.StartEndSeparatorChars ->
                    sequence {
                        yield(separatorStrategy.start)
                        yieldAll(encodedWatermark)
                        yield(separatorStrategy.end)
                    }
            }

        return separatedWatermark
    }

    companion object {
        private const val SOURCE = "PlainTextWatermarker"
    }

    class IncompleteWatermarkWarning : Event.Warning("$SOURCE.getWatermark") {
        /** Returns a String explaining the event */
        override fun getMessage() = "Could not restore a complete watermark!"
    }

    class OversizedWatermarkWarning(
        private val watermarkSize: Int,
        private val insertableSize: Int,
    ) : Event.Warning("$SOURCE.addWatermark") {
        /** Returns a String explaining the event */
        override fun getMessage() =
            "Could only insert $insertableSize of $watermarkSize bytes from the Watermark into " +
                "the cover."
    }

    class ContainsAlphabetCharsError(val chars: Sequence<Char>) :
        Event.Error("$SOURCE.addWatermark") {
        /** Returns a String explaining the event */
        override fun getMessage(): String {
            val containedChars =
                chars.joinToString(prefix = "[", separator = ",", postfix = "]") { char ->
                    "'${char.toUnicodeRepresentation()}'"
                }

            return "The input contains characters of the watermark " +
                "transcoding alphabet. It is only possible to add a watermark to an input " +
                "that doesn't contain any watermark. Adding another watermark would potentially " +
                "make the input unusable! Maybe the input already contains a watermark?\n\n" +
                "Contained Chars:\n" +
                "$containedChars."
        }
    }

    class Success(
        val startPositions: List<Int>,
        val incomplete: Boolean = false,
    ) : Event.Success() {
        /** Returns a String explaining the event */
        override fun getMessage(): String =
            "Added complete Watermark ${if (incomplete) {
                startPositions.size - 1
            } else {
                startPositions.size
            }
            } times. Starting positions: $startPositions."
    }

    class AlphabetContainsSeparatorError(val chars: List<Char>) : Event.Error(SOURCE) {
        override fun getMessage(): String {
            val containedChars =
                chars.joinToString(prefix = "[", separator = ",", postfix = "]") { char ->
                    "'${char.toUnicodeRepresentation()}'"
                }
            return "The alphabet contains separator char(s): $containedChars"
        }
    }
}

/** Returns [watermarks] without duplicates */
fun <T : Watermark> squashWatermarks(watermarks: List<T>): List<T> {
    return watermarks.toSet().toList()
}
