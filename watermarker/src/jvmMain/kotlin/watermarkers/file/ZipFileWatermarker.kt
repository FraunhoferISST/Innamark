/*
 * Copyright (c) 2023-2025 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.
 *
 * This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 * that can be found in the LICENSE file.
 */

package de.fraunhofer.isst.innamark.watermarker.watermarkers.file

import de.fraunhofer.isst.innamark.watermarker.types.files.ZipFile
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
import de.fraunhofer.isst.innamark.watermarker.watermarkers.text.squashWatermarks

const val ZIP_WATERMARK_ID: UShort = 0x8777u

object ZipFileWatermarker : FileWatermarker<ZipFile> {
    const val SOURCE = "ZipFileWatermarker"

    /** Returns the name of the FileWatermark. Used in Event messages. */
    override fun getSource(): String = SOURCE

    /**
     * Adds a watermark created from [watermark] ByteArray to file content at [source] and writes
     * it to [target].
     * Returns an error if the size of the ExtraFields exceed UShort::MAX
     */
    override fun addWatermark(
        source: String,
        target: String,
        watermark: ByteArray,
        fileType: String?
    ): Status {
        val supportedFileType =
            with(SupportedFileType.getFileType(source, fileType)) {
                value ?: return into()
            }
        if (supportedFileType != SupportedFileType.Zip) {
            return Status(SupportedFileType.WrongTypeError(supportedFileType.toString()))
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

        status.appendStatus(file.header.addExtraField(ZIP_WATERMARK_ID, watermark.toList()))

        if (!status.isError) {
            status.appendStatus(file.writeToFile(target))
        }
        return status
    }

    /**
     * Adds a watermark created from [watermark] String to file content at [source] and writes
     * it to [target].
     * Returns an error if the size of the ExtraFields exceed UShort::MAX
     */
    override fun addWatermark(
        source: String,
        target: String,
        watermark: String,
        fileType: String?
    ): Status {
        return addWatermark(source, target, watermark.encodeToByteArray(), fileType)
    }

    /**
     * Adds a watermark object to file content at [source] and writes it to [target].
     * Returns an error if the size of the ExtraFields exceed UShort::MAX
     */
    override fun addWatermark(
        source: String,
        target: String,
        watermark: Watermark,
        fileType: String?
    ): Status {
        return addWatermark(source, target, watermark.watermarkContent, fileType)
    }

    /**
     * Adds a watermark created from [innamarkTagBuilder] to file content at [source] and writes
     * it to [target].
     * Returns an error if the size of the ExtraFields exceed UShort::MAX
     */
    override fun addWatermark(
        source: String,
        target: String,
        innamarkTagBuilder: InnamarkTagBuilder,
        fileType: String?
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
            return SupportedFileType.WrongTypeError(supportedFileType.toString()).into(false)
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

        for (extraField in file.header.extraFields) {
            if (extraField.id == ZIP_WATERMARK_ID) return status.into(true)
        }
        return status.into(false)
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
            return SupportedFileType.WrongTypeError(supportedFileType.toString()).into(listOf())
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

        var watermarks = ArrayList<Watermark>()
        for (extraField in file.header.extraFields) {
            if (extraField.id == ZIP_WATERMARK_ID) {
                watermarks.add(Watermark(extraField.data.toByteArray()))
            }
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
                decoded.appendStatus(Status(StringDecodeWarning("ZipFileWatermarker")))
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
        fileType: String?
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
        val supportedFileType =
            with(SupportedFileType.getFileType(source, fileType)) {
                value ?: return into()
            }
        if (supportedFileType != SupportedFileType.Text) {
            return Status(SupportedFileType.WrongTypeError(supportedFileType.toString()))
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

        file.header.removeExtraFields(ZIP_WATERMARK_ID)
        return status
    }

    /**
     * Parses [bytes] as zip file.
     * Parsing includes separating the header from the content and parsing the header.
     *
     * Returns errors if it cannot parse [bytes] as zip file.
     * Returns warnings if the parser finds unexpected structures but is still able to parse it
     */
    override fun parseBytes(bytes: ByteArray): Result<ZipFile> {
        return ZipFile.fromBytes(bytes)
    }
}
