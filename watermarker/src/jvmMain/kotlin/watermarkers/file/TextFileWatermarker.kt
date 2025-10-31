/*
 * Copyright (c) 2023-2025 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.
 *
 * This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 * that can be found in the LICENSE file.
 */

package de.fraunhofer.isst.innamark.watermarker.watermarkers.file

import de.fraunhofer.isst.innamark.watermarker.types.files.TextFile
import de.fraunhofer.isst.innamark.watermarker.types.files.writeToFile
import de.fraunhofer.isst.innamark.watermarker.types.responses.Event
import de.fraunhofer.isst.innamark.watermarker.types.responses.Result
import de.fraunhofer.isst.innamark.watermarker.types.responses.Status
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.InnamarkTag
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.InnamarkTagBuilder
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.Watermark
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.Watermark.MultipleMostFrequentWarning
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.Watermark.StringDecodeWarning
import de.fraunhofer.isst.innamark.watermarker.utils.FileHandling.Companion.readFile
import de.fraunhofer.isst.innamark.watermarker.utils.SupportedFileType
import de.fraunhofer.isst.innamark.watermarker.utils.getFileType
import de.fraunhofer.isst.innamark.watermarker.watermarkers.text.DefaultTranscoding
import de.fraunhofer.isst.innamark.watermarker.watermarkers.text.PlainTextWatermarker
import de.fraunhofer.isst.innamark.watermarker.watermarkers.text.SeparatorStrategy
import de.fraunhofer.isst.innamark.watermarker.watermarkers.text.Transcoding

/**
 * Implementation of [FileWatermarker] for watermarking Text files.
 *
 * Takes optional arguments to customize behavior:
 * - [transcoding]: for defining the watermarking alphabet, encoding, and decoding.
 * - [separatorStrategy]: for defining how multiple watermarks are separated.
 * - [placement]: function for finding positions where transcoding alphabet characters are inserted.
 */
class TextFileWatermarker(
    private val transcoding: Transcoding = DefaultTranscoding,
    private val separatorStrategy: SeparatorStrategy =
        SeparatorStrategy.SingleSeparatorChar(
            DefaultTranscoding.SEPARATOR_CHAR,
        ),
    val placement: (String) -> List<Int> = { string ->
        sequence {
            for ((index, char) in string.withIndex()) {
                if (char == ' ') yield(index)
            }
        }.toMutableList() // mutable for JS compatibility on empty lists
    },
) : FileWatermarker<TextFile> {
    private val watermarker = PlainTextWatermarker(transcoding, separatorStrategy, placement)

    /**
     * Adds a watermark created from [watermark] ByteArray to the file content at [source]
     * and writes it to [target].
     *
     * Returns a warning if the watermark does not fit at least a single time into the file.
     * Returns an error if the text file contains a character from the transcoding alphabet.
     */
    override fun addWatermark(
        source: String,
        target: String,
        watermark: ByteArray,
        wrap: Boolean,
        fileType: String?,
    ): Status {
        val fileStatus = checkFileType(source, target, fileType)
        if (!fileStatus.isSuccess) {
            return fileStatus
        }

        val (status, bytes) =
            with(readFile(source)) {
                status to (value ?: return into())
            }

        val file =
            with(parseBytes(bytes)) {
                status.appendStatus(this.status)
                value ?: return status
            }

        val result = watermarker.addWatermark(file.content, watermark, wrap = wrap)
        status.appendStatus(result.status)
        if (result.hasValue) file.content = result.value!!

        if (!status.isError) {
            status.appendStatus(file.writeToFile(target))
        }
        return status
    }

    /**
     * Adds a watermark created from [watermark] String to the file content at [source]
     * and writes it to [target].
     *
     * Returns a warning if the watermark does not fit at least a single time into the file.
     * Returns an error if the text file contains a character from the transcoding alphabet.
     */
    override fun addWatermark(
        source: String,
        target: String,
        watermark: String,
        fileType: String?,
    ): Status {
        return addWatermark(source, target, watermark.encodeToByteArray(), true, fileType)
    }

    /**
     * Adds a [watermark] object to the file content at [source] and writes it to [target].
     *
     * Returns a warning if the watermark does not fit at least a single time into the file.
     * Returns an error if the text file contains a character from the transcoding alphabet.
     */
    override fun addWatermark(
        source: String,
        target: String,
        watermark: Watermark,
        fileType: String?,
    ): Status {
        return addWatermark(source, target, watermark.watermarkContent, false, fileType)
    }

    /**
     * Adds a watermark created from [innamarkTagBuilder] to the file content at [source]
     * and writes it to [target].
     *
     * Returns a warning if the watermark does not fit at least a single time into the file.
     * Returns an error if the text file contains a character from the transcoding alphabet.
     */
    override fun addWatermark(
        source: String,
        target: String,
        innamarkTagBuilder: InnamarkTagBuilder,
        fileType: String?,
    ): Status {
        return addWatermark(source, target, innamarkTagBuilder.finish(), fileType)
    }

    /** Checks if the file at [source] contains a watermark */
    override fun containsWatermark(
        source: String,
        fileType: String?,
    ): Result<Boolean> {
        val supportedFileType =
            with(SupportedFileType.getFileType(source, fileType)) {
                value ?: return into<_>()
            }
        if (supportedFileType != SupportedFileType.Text) {
            return SupportedFileType.WrongTypeError(
                supportedFileType.toString(),
                SOURCE,
            ).into(false)
        }

        val (status, bytes) =
            with(readFile(source)) {
                status to (value ?: return into<_>())
            }

        val file =
            with(parseBytes(bytes)) {
                status.appendStatus(this.status)
                value ?: return status.into()
            }

        val contains = watermarker.containsWatermark(file.content)
        return status.into(contains)
    }

    /**
     * Returns a [Result] containing a list of [Watermark]s in the file at [source]. Attempts
     * to parse Watermarks found into [InnamarkTag]s and returns them instead if
     * [InnamarkTag.validate] returns [Event.Success] on all Watermarks.
     *
     * When [squash] is true: watermarks with the same content are merged.
     * When [singleWatermark] is true: only the most frequent watermark is returned.
     */
    override fun getWatermarks(
        source: String,
        fileType: String?,
        squash: Boolean,
        singleWatermark: Boolean,
    ): Result<List<Watermark>> {
        val supportedFileType =
            with(SupportedFileType.getFileType(source, fileType)) {
                value ?: return into<_>()
            }
        if (supportedFileType != SupportedFileType.Text) {
            return SupportedFileType.WrongTypeError(
                supportedFileType.toString(),
                SOURCE,
            ).into(listOf())
        }

        val (status, bytes) =
            with(readFile(source)) {
                status to (value ?: return into<_>())
            }

        val file =
            with(parseBytes(bytes)) {
                status.appendStatus(this.status)
                value ?: return status.into()
            }

        val watermarks =
            with(watermarker.getWatermarks(file.content, squash, singleWatermark)) {
                status.appendStatus(this.status)
                value
            }

        return status.into(watermarks)
    }

    /**
     * Returns a [Result] containing the most frequent Watermark in the file at [source] as a String.
     *
     * Result contains an empty String if no Watermarks were found.
     * Result contains a [MultipleMostFrequentWarning] in cases where an unambiguous Watermark could not be extracted.
     * Result contains a [StringDecodeWarning] in cases where a byte cannot be read as UTF-8.
     */
    override fun getWatermarkAsString(
        source: String,
        fileType: String?,
    ): Result<String> {
        val watermarks = getWatermarks(source, fileType, squash = false, singleWatermark = true)
        if (watermarks.value?.isNotEmpty() ?: return Result.success("")) {
            val decoded =
                watermarks.status.into(
                    watermarks.value[0].watermarkContent
                        .decodeToString(),
                )
            if (decoded.value!!.contains('\uFFFD')) {
                decoded.appendStatus(Status(StringDecodeWarning("TextFileWatermarker")))
            }
            return decoded
        } else {
            return Result.success("")
        }
    }

    /**
     * Returns a [Result] containing the most frequent Watermark in the file at [source] as a
     * ByteArray.
     *
     * Result contains an empty ByteArray if no Watermarks were found.
     * Result contains a [MultipleMostFrequentWarning] in cases where an unambiguous Watermark could not be extracted.
     */
    override fun getWatermarkAsByteArray(
        source: String,
        fileType: String?,
    ): Result<ByteArray> {
        val watermarks = getWatermarks(source, fileType, squash = false, singleWatermark = true)
        if (watermarks.value?.isNotEmpty() ?: return Result.success(ByteArray(0))) {
            return watermarks.status.into(watermarks.value[0].watermarkContent)
        } else {
            return Result.success(ByteArray(0))
        }
    }

    /**
     * Removes all watermarks in the file at [source] and writes the result to [target].
     */
    override fun removeWatermarks(
        source: String,
        target: String,
        fileType: String?,
    ): Status {
        val fileStatus = checkFileType(source, target, fileType)
        if (!fileStatus.isSuccess) {
            return fileStatus
        }
        val (status, bytes) =
            with(readFile(source)) {
                status to (value ?: return into())
            }

        val file =
            with(parseBytes(bytes)) {
                status.appendStatus(this.status)
                value ?: return status
            }

        val result = watermarker.removeWatermarks(file.content)
        status.appendStatus(result.status)
        if (result.hasValue) file.content = result.value!!

        if (!status.isError) {
            status.appendStatus(file.writeToFile(target))
        }
        return status
    }

    /** Parses [bytes] as text and returns it as TextFile */
    override fun parseBytes(bytes: ByteArray): Result<TextFile> {
        return TextFile.fromBytes(bytes)
    }

    companion object {
        private const val SOURCE = "TextFileWatermarker"
    }

    /** Returns the name of the FileWatermarker. Used in Event messages. */
    override fun getSource(): String = SOURCE

    private fun checkFileType(
        source: String,
        target: String,
        fileType: String?,
    ): Status {
        val supportedSourceFileType =
            with(SupportedFileType.getFileType(source, fileType)) {
                value ?: return into()
            }
        if (supportedSourceFileType != SupportedFileType.Text) {
            return Status(
                SupportedFileType.WrongTypeError(supportedSourceFileType.toString(), SOURCE),
            )
        }
        val supportedTargetFileType =
            with(SupportedFileType.getFileType(target, fileType)) {
                value ?: return into()
            }
        if (supportedTargetFileType != SupportedFileType.Text) {
            return Status(
                SupportedFileType.WrongTypeError(supportedTargetFileType.toString(), SOURCE),
            )
        }
        return Status.success()
    }
}
