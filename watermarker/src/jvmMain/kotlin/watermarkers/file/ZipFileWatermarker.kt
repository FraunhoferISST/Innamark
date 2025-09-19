/*
 * Copyright (c) 2023-2025 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.
 *
 * This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 * that can be found in the LICENSE file.
 */

package de.fraunhofer.isst.innamark.watermarker.watermarkers.file

import de.fraunhofer.isst.innamark.watermarker.types.files.ZipFile
import de.fraunhofer.isst.innamark.watermarker.types.responses.Result
import de.fraunhofer.isst.innamark.watermarker.types.responses.Status
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.InnamarkTagBuilder
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.Watermark
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.Watermark.MultipleMostFrequentWarning
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.Watermark.StringDecodeWarning
import de.fraunhofer.isst.innamark.watermarker.watermarkers.text.squashWatermarks

const val ZIP_WATERMARK_ID: UShort = 0x8777u

object ZipFileWatermarker : FileWatermarker<ZipFile> {
    const val SOURCE = "ZipFileWatermarker"

    /** Returns the name of the FileWatermark. Used in Event messages. */
    override fun getSource(): String = SOURCE

    /**
     * Adds a watermark created from [watermark] ByteArray to [file]
     * Returns an error if the size of the ExtraFields exceed UShort::MAX
     */
    override fun addWatermark(
        file: ZipFile,
        watermark: ByteArray,
    ): Status {
        return file.header.addExtraField(ZIP_WATERMARK_ID, watermark.toList())
    }

    /**
     * Adds a watermark created from [watermark] String to [file]
     * Returns an error if the size of the ExtraFields exceed UShort::MAX
     */
    fun addWatermark(
        file: ZipFile,
        watermark: String,
    ): Status {
        return addWatermark(file, watermark.encodeToByteArray())
    }

    /**
     * Adds a [watermark] to [file]
     * Returns an error if the size of the ExtraFields exceed UShort::MAX
     */
    override fun addWatermark(
        file: ZipFile,
        watermark: Watermark,
    ): Status {
        return addWatermark(file, watermark.watermarkContent)
    }

    /**
     * Adds a watermark created from [innamarkTagBuilder] to [file]
     * Returns an error if the size of the ExtraFields exceed UShort::MAX
     */
    override fun addWatermark(
        file: ZipFile,
        innamarkTagBuilder: InnamarkTagBuilder,
    ): Status {
        return addWatermark(file, innamarkTagBuilder.finish())
    }

    /** Checks if [file] contains a watermark */
    override fun containsWatermark(file: ZipFile): Boolean {
        for (extraField in file.header.extraFields) {
            if (extraField.id == ZIP_WATERMARK_ID) return true
        }
        return false
    }

    /**
     * Returns a [Result] containing the most frequent Watermark in [cover] as a String
     *
     * Result contains an empty String if no Watermarks were found.
     * Result contains a [MultipleMostFrequentWarning] in cases where an unambiguous Watermark could not be extracted.
     * Result contains a [StringDecodeWarning] in cases where a byte cannot be read as UTF-8.
     */
    fun getWatermarkAsString(cover: ZipFile): Result<String> {
        val watermarks = getWatermarks(cover, false, true)
        if (watermarks.value?.isNotEmpty() ?: return Result.success("")) {
            val decoded =
                watermarks.status.into(
                    watermarks.value[0].watermarkContent.decodeToString(),
                )
            if (decoded.value!!.contains('\uFFFD')) {
                decoded.appendStatus(Status(StringDecodeWarning(SOURCE)))
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
     */
    fun getWatermarkAsByteArray(cover: ZipFile): Result<ByteArray> {
        val watermarks = getWatermarks(cover, false, true)
        if (watermarks.value?.isNotEmpty() ?: return Result.success(ByteArray(0))) {
            return watermarks.status.into(watermarks.value[0].watermarkContent)
        } else {
            return Result.success(ByteArray(0))
        }
    }

    /**
     * Returns all watermarks in [file]
     * When [squash] is true: watermarks with the same content are merged.
     * When [singleWatermark] is true: only the most frequent watermark is returned.
     */
    override fun getWatermarks(
        file: ZipFile,
        squash: Boolean,
        singleWatermark: Boolean,
    ): Result<List<Watermark>> {
        val status: Status = Status.success()
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
        return status.into(watermarks)
    }

    /**
     * Removes all watermarks in [file] and returns them
     * When [squash] is true: watermarks with the same content are merged.
     * When [singleWatermark] is true: only the most frequent watermark is returned.
     */
    override fun removeWatermarks(
        file: ZipFile,
        squash: Boolean,
        singleWatermark: Boolean,
    ): Result<List<Watermark>> {
        val status: Status = Status.success()
        var watermarks = ArrayList<Watermark>()
        for (extraField in file.header.removeExtraFields(ZIP_WATERMARK_ID)) {
            watermarks.add(Watermark(extraField.data.toByteArray()))
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
        return status.into(watermarks)
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
