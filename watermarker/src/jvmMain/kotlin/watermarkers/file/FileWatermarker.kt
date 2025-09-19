/*
 * Copyright (c) 2023-2025 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.
 *
 * This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 * that can be found in the LICENSE file.
 */

package de.fraunhofer.isst.innamark.watermarker.watermarkers.file

import de.fraunhofer.isst.innamark.watermarker.types.files.WatermarkableFile
import de.fraunhofer.isst.innamark.watermarker.types.responses.Result
import de.fraunhofer.isst.innamark.watermarker.types.responses.Status
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.InnamarkTag
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.InnamarkTagBuilder
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.Watermark
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.toInnamarkTags
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.toTextWatermarks

interface FileWatermarker<File : WatermarkableFile> {
    fun addWatermark(
        file: File,
        watermark: ByteArray,
    ): Status

    /** Adds a [watermark] to [file] */
    fun addWatermark(
        file: File,
        watermark: Watermark,
    ): Status {
        return addWatermark(file, watermark.watermarkContent)
    }

    /** Adds a [innamarkTagBuilder] to [file] */
    fun addWatermark(
        file: File,
        innamarkTagBuilder: InnamarkTagBuilder,
    ): Status {
        return addWatermark(file, innamarkTagBuilder.finish())
    }

    /** Checks if [file] contains watermarks */
    fun containsWatermark(file: File): Boolean

    /**
     * Returns all watermarks in [file]
     * When [squash] is true: watermarks with the same content are merged.
     * When [singleWatermark] is true: only the most frequent watermark is returned.
     */
    fun getWatermarks(
        file: File,
        squash: Boolean = false,
        singleWatermark: Boolean = false,
    ): Result<List<Watermark>>

    /**
     * Returns all watermarks in [file] as InnamarkTags.
     *
     * When [squash] is true: watermarks with the same content are merged.
     * When [singleWatermark] is true: only the most frequent watermark is returned.
     * Returns a warning if some watermarks could not be converted to InnamarkTags.
     * Returns an error if no watermark could be converted to a InnamarkTag.
     */
    fun getInnamarkTags(
        file: File,
        squash: Boolean = false,
        singleWatermark: Boolean = false,
    ): Result<List<InnamarkTag>> =
        getWatermarks(file, squash, singleWatermark).toInnamarkTags(
            "${getSource()}" +
                ".getInnamarks",
        )

    /**
     * Returns all watermarks in [file] as TextWatermarks.
     *
     * When [squash] is true: watermarks with the same content are merged.
     * When [singleWatermark] is true: only the most frequent watermark is returned.
     * When [errorOnInvalidUTF8] is true: invalid bytes sequences cause an error.
     *                           is false: invalid bytes sequences are replace with the char �.
     *
     * Returns a warning if some watermarks could not be converted to InnamarkTags.
     * Returns an error if no watermark could be converted to a InnamarkTag.
     *
     * Returns a warning if some InnamarkTags could not be converted to TextWatermarks.
     * Returns an error if no InnamarkTag could be converted to a TextWatermark.
     */
    fun getTextWatermarks(
        file: File,
        squash: Boolean = false,
        singleWatermark: Boolean = false,
        errorOnInvalidUTF8: Boolean = false,
    ): Result<List<InnamarkTagBuilder>> =
        getWatermarks(file, squash, singleWatermark).toTextWatermarks(
            errorOnInvalidUTF8,
            "${getSource()}" +
                ".getTextWatermarks",
        )

    /**
     * Removes all watermarks in [file] and returns them
     * When [squash] is true: watermarks with the same content are merged.
     * When [singleWatermark] is true: only the most frequent watermark is returned.
     */
    fun removeWatermarks(
        file: File,
        squash: Boolean = false,
        singleWatermark: Boolean = false,
    ): Result<List<Watermark>>

    /** Parses [bytes] as File */
    fun parseBytes(bytes: ByteArray): Result<File>

    /** Returns the name of the FileWatermark. Used in Event messages. */
    fun getSource(): String
}
