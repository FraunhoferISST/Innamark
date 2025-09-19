/*
 * Copyright (c) 2023-2025 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.
 *
 * This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 * that can be found in the LICENSE file.
 */

package de.fraunhofer.isst.innamark.watermarker.watermarkers.file

import de.fraunhofer.isst.innamark.watermarker.types.files.TextFile
import de.fraunhofer.isst.innamark.watermarker.types.responses.Result
import de.fraunhofer.isst.innamark.watermarker.types.responses.Status
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.InnamarkTagBuilder
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.Watermark
import de.fraunhofer.isst.innamark.watermarker.watermarkers.text.DefaultTranscoding
import de.fraunhofer.isst.innamark.watermarker.watermarkers.text.PlainTextWatermarker
import de.fraunhofer.isst.innamark.watermarker.watermarkers.text.SeparatorStrategy
import de.fraunhofer.isst.innamark.watermarker.watermarkers.text.Transcoding
import kotlin.jvm.JvmStatic

class TextFileWatermarker(
    private val transcoding: Transcoding,
    private val separatorStrategy: SeparatorStrategy,
    val placement: (String) -> List<Int>,
) : FileWatermarker<TextFile> {
    // Build a list of all chars that are contained in a watermark
    private val textWatermarker = PlainTextWatermarker()
    private val fullAlphabet: List<Char> =
        when (separatorStrategy) {
            is SeparatorStrategy.SkipInsertPosition -> transcoding.alphabet
            is SeparatorStrategy.SingleSeparatorChar ->
                listOf(separatorStrategy.char) + transcoding.alphabet

            is SeparatorStrategy.StartEndSeparatorChars ->
                listOf(separatorStrategy.start, separatorStrategy.end) + transcoding.alphabet
        }

    /**
     * Adds a watermark created from [watermark] to [file].
     *
     * Returns a warning if the watermark does not fit at least a single time into the file.
     * Returns an error if the text file contains a character from the transcoding alphabet.
     */
    override fun addWatermark(
        file: TextFile,
        watermark: ByteArray,
    ): Status {
        val text = file.content
        val result = textWatermarker.addWatermark(text, watermark)
        if (result.hasValue) file.content = result.value!!
        return result.status
    }

    /**
     * Adds a watermark created from [watermark] to [file].
     *
     * Returns a warning if the watermark does not fit at least a single time into the file.
     * Returns an error if the text file contains a character from the transcoding alphabet.
     */
    fun addWatermark(
        file: TextFile,
        watermark: String,
    ): Status {
        val text = file.content
        val result = textWatermarker.addWatermark(text, watermark)
        if (result.hasValue) file.content = result.value!!
        return result.status
    }

    /**
     * Adds a [watermark] to [file].
     *
     * Returns a warning if the watermark does not fit at least a single time into the file.
     * Returns an error if the text file contains a character from the transcoding alphabet.
     */
    override fun addWatermark(
        file: TextFile,
        watermark: Watermark,
    ): Status {
        val text = file.content
        val result = textWatermarker.addWatermark(text, watermark)
        if (result.hasValue) file.content = result.value!!
        return result.status
    }

    /**
     * Adds a watermark created from [innamarkTagBuilder] to [file].
     *
     * Returns a warning if the watermark does not fit at least a single time into the file.
     * Returns an error if the text file contains a character from the transcoding alphabet.
     */
    override fun addWatermark(
        file: TextFile,
        innamarkTagBuilder: InnamarkTagBuilder,
    ): Status {
        val text = file.content
        val result = textWatermarker.addWatermark(text, innamarkTagBuilder.finish())
        if (result.hasValue) file.content = result.value!!
        return result.status
    }

    /** Checks if [file] contains any [Char] from full watermarking alphabet */
    override fun containsWatermark(file: TextFile): Boolean =
        textWatermarker.containsWatermark(file.content)

    /**
     * Returns all watermarks in [file]
     * When [squash] is true: watermarks with the same content are merged.
     * When [singleWatermark] is true: only the most frequent watermark is returned.
     * */
    override fun getWatermarks(
        file: TextFile,
        squash: Boolean,
        singleWatermark: Boolean,
    ): Result<List<Watermark>> =
        textWatermarker.getWatermarks(file.content, squash, singleWatermark)

    /**
     * Removes all watermarks in [file] and returns them.
     *
     * When [squash] is true: watermarks with the same content are merged.
     * When [singleWatermark] is true: only the most frequent watermark is returned.
     * Returns a warning if getWatermarks() returns a warning or error.
     */
    override fun removeWatermarks(
        file: TextFile,
        squash: Boolean,
        singleWatermark: Boolean,
    ): Result<List<Watermark>> {
        val text = file.content
        // get Watermarks
        val (status, watermarks) =
            with(textWatermarker.getWatermarks(text, squash, singleWatermark)) {
                status to (value ?: listOf())
            }

        // Replace all chars from the file that are in the transcoding alphabet with a whitespace
        val result = textWatermarker.removeWatermarks(text)
        if (result.hasValue) file.content = result.value!!

        if (!status.isSuccess) {
            status.addEvent(PlainTextWatermarker.RemoveWatermarksGetProblemWarning(), true)
        }

        return status.into(watermarks)
    }

    /** Parses [bytes] as text and returns it as TextFile */
    override fun parseBytes(bytes: ByteArray): Result<TextFile> {
        return TextFile.fromBytes(bytes)
    }

    /** Counts the minimum number of insert positions needed in a text to insert the [watermark] */
    fun getMinimumInsertPositions(watermark: ByteArray): Int =
        textWatermarker.getMinimumInsertPositions(watermark)

    /** Counts the minimum number of insert positions needed in a text to insert the [watermark] */
    fun getMinimumInsertPositions(watermark: Watermark): Int =
        getMinimumInsertPositions(watermark.watermarkContent)

    /**
     * Counts the minimum number of insert positions needed in a text to insert the
     * [innamarkTagBuilder]
     */
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
        private const val SOURCE = "TextFileWatermarker"

        /** Returns the builder for TextWatermarker */
        @JvmStatic
        fun builder(): TextFileWatermarkerBuilder = TextFileWatermarkerBuilder()

        /** Returns an instance of TextWatermarker in default configuration */
        @JvmStatic
        fun default(): TextFileWatermarker = TextFileWatermarkerBuilder().build().value!!
    }

    override fun getSource(): String = SOURCE
}

class TextFileWatermarkerBuilder {
    private var transcoding: Transcoding = DefaultTranscoding
    private var separatorStrategy: SeparatorStrategy =
        SeparatorStrategy.SingleSeparatorChar(DefaultTranscoding.SEPARATOR_CHAR)

    /** Yields all positions where a Char of the watermark can be inserted */
    private var placement: (string: String) -> List<Int> = { string ->
        sequence {
            for ((index, char) in string.withIndex()) {
                if (char == ' ') yield(index)
            }
        }.toMutableList() // mutable for JS compatibility on empty lists
    }

    /** Sets a custom transcoding alphabet */
    fun setTranscoding(transcoding: Transcoding): TextFileWatermarkerBuilder {
        this.transcoding = transcoding
        return this
    }

    /** Set a custom separator strategy */
    fun setSeparatorStrategy(separatorStrategy: SeparatorStrategy): TextFileWatermarkerBuilder {
        this.separatorStrategy = separatorStrategy
        return this
    }

    /** Sets a custom placement function used to identify insertion positions */
    fun setPlacement(placement: (String) -> List<Int>): TextFileWatermarkerBuilder {
        this.placement = placement
        return this
    }

    /** Validates the TextWatermarker configuration */
    private fun validate(): Status {
        val status = Status.success()

        when (val separatorStrategy = separatorStrategy) {
            is SeparatorStrategy.SkipInsertPosition -> {}
            is SeparatorStrategy.SingleSeparatorChar -> {
                if (separatorStrategy.char in transcoding.alphabet) {
                    status.addEvent(
                        PlainTextWatermarker.AlphabetContainsSeparatorError(
                            listOf(separatorStrategy.char),
                        ),
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
                    status.addEvent(PlainTextWatermarker.AlphabetContainsSeparatorError(list))
                }
            }
        }

        return status
    }

    /**
     * Creates a TextWatermarker.
     *
     * Fails if transcoding alphabet contains separator chars.
     */
    fun build(): Result<TextFileWatermarker> {
        val status = validate()
        return if (status.isError) {
            return status.into()
        } else {
            status.into(TextFileWatermarker(transcoding, separatorStrategy, placement))
        }
    }

    companion object {
        private const val SOURCE = "TextWatermarkerBuilder"
    }
}
