/*
 * Copyright (c) 2025 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.
 *
 * This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 * that can be found in the LICENSE file.
 */
package de.fraunhofer.isst.innamark.watermarker.binaryWatermarkers

import de.fraunhofer.isst.innamark.watermarker.files.ZipFile
import de.fraunhofer.isst.innamark.watermarker.returnTypes.Result
import de.fraunhofer.isst.innamark.watermarker.returnTypes.Status
import de.fraunhofer.isst.innamark.watermarker.squashWatermarks
import de.fraunhofer.isst.innamark.watermarker.watermarks.Watermark
import de.fraunhofer.isst.innamark.watermarker.watermarks.Watermark.MultipleMostFrequentWarning
import de.fraunhofer.isst.innamark.watermarker.watermarks.Watermark.StringDecodeWarning
import kotlin.collections.toList

/**
 * Implementation of [BinaryWatermarker] for [ZipFile] covers
 */
class ZipWatermarkerImpl : BinaryWatermarker<ZipFile> {
    companion object {
        const val SOURCE = "ZipWatermarkerImpl"
        const val ZIP_WATERMARK_ID: UShort = 0x8777u
    }

    /**
     * Adds a watermark created from [watermark] String to [cover]
     * Returns an error if the size of the ExtraFields exceed UShort::MAX
     */
    override fun addWatermark(
        cover: ZipFile,
        watermark: String,
    ): Result<ZipFile> {
        return addWatermark(cover, watermark.encodeToByteArray())
    }

    /**
     * Adds a watermark created from [watermark] ByteArray to [cover]
     * Returns an error if the size of the ExtraFields exceed UShort::MAX
     */
    override fun addWatermark(
        cover: ZipFile,
        watermark: ByteArray,
    ): Result<ZipFile> {
        return cover.header.addExtraField(ZIP_WATERMARK_ID, watermark.toList()).into(cover)
    }

    /**
     * Adds watermark object [watermark] to [cover]
     * Returns an error if the size of the ExtraFields exceed UShort::MAX
     */
    override fun addWatermark(
        cover: ZipFile,
        watermark: Watermark,
    ): Result<ZipFile> {
        return addWatermark(cover, watermark.watermarkContent)
    }

    /** Returns a [Boolean] indicating whether [cover] contains watermarks */
    override fun containsWatermark(cover: ZipFile): Boolean {
        for (extraField in cover.header.extraFields) {
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
    override fun getWatermarkAsString(cover: ZipFile): Result<String> {
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
    override fun getWatermarkAsByteArray(cover: ZipFile): Result<ByteArray> {
        val watermarks = getWatermarks(cover, false, true)
        if (watermarks.value?.isNotEmpty() ?: return Result.success(ByteArray(0))) {
            return watermarks.status.into(watermarks.value[0].watermarkContent)
        } else {
            return Result.success(ByteArray(0))
        }
    }

    /**
     * Returns a [Result] containing a list of [Watermark]s in [cover]
     *
     * When [squash] is true: watermarks with the same content are merged.
     * When [singleWatermark] is true: only the most frequent watermark is returned.
     */
    override fun getWatermarks(
        cover: ZipFile,
        squash: Boolean,
        singleWatermark: Boolean,
    ): Result<List<Watermark>> {
        val status: Status = Status.success()
        var watermarks = ArrayList<Watermark>()
        for (extraField in cover.header.extraFields) {
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

    /** Removes all watermarks from [cover] and returns a [Result] containing the cleaned cover */
    override fun removeWatermark(cover: ZipFile): Result<ZipFile> {
        cover.header.removeExtraFields(ZIP_WATERMARK_ID)
        return Result.success(cover)
    }
}
