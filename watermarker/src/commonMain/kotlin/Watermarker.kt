/*
 * Copyright (c) 2023-2025 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.
 *
 * This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 * that can be found in the LICENSE file.
 */
package de.fraunhofer.isst.innamark.watermarker

import de.fraunhofer.isst.innamark.watermarker.fileWatermarkers.FileWatermarker
import de.fraunhofer.isst.innamark.watermarker.fileWatermarkers.TextFileWatermarker
import de.fraunhofer.isst.innamark.watermarker.fileWatermarkers.ZipFileWatermarker
import de.fraunhofer.isst.innamark.watermarker.files.TextFile
import de.fraunhofer.isst.innamark.watermarker.returnTypes.Event
import de.fraunhofer.isst.innamark.watermarker.returnTypes.Result
import de.fraunhofer.isst.innamark.watermarker.watermarks.InnamarkTag
import de.fraunhofer.isst.innamark.watermarker.watermarks.InnamarkTagBuilder
import de.fraunhofer.isst.innamark.watermarker.watermarks.TextWatermark
import de.fraunhofer.isst.innamark.watermarker.watermarks.Watermark
import de.fraunhofer.isst.innamark.watermarker.watermarks.toInnamarkTags
import de.fraunhofer.isst.innamark.watermarker.watermarks.toTextWatermarks
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

@JsExport
sealed class SupportedFileType {
    abstract val watermarker: FileWatermarker<*>

    object Text : SupportedFileType() {
        override var watermarker: TextFileWatermarker = TextFileWatermarker.default()
    }

    object Zip : SupportedFileType() {
        override var watermarker: ZipFileWatermarker = ZipFileWatermarker
    }

    companion object {
        private val extensionMap =
            mutableMapOf(
                "zip" to Zip,
                "jar" to Zip,
                "txt" to Text,
                "md" to Text,
            )

        /*
         * TODO: Write test when builder pattern is implemented for
         *  TextWatermarker / ZipWatermarker
         */

        /** Returns a variant of SupportedFileType if [extension] is supported */
        @JvmStatic
        fun fromExtension(extension: String): Result<SupportedFileType> {
            val fileType = extensionMap[extension]
            return if (fileType == null) {
                UnsupportedTypeError(extension).into<_>()
            } else {
                Result.success(fileType)
            }
        }

        /** Registers an [extension] to a variant of SupportedFileType */
        @JvmStatic
        fun registerExtension(
            extension: String,
            fileType: SupportedFileType,
        ) {
            extensionMap[extension] = fileType
        }

        /** Registers [watermarker] for zip files */
        @JvmStatic
        fun registerZipWatermarker(watermarker: ZipFileWatermarker) {
            Zip.watermarker = watermarker
        }

        /** Registers [watermarker] for TextWatermarker */
        @JvmStatic
        fun registerTextWatermarker(watermarker: TextFileWatermarker) {
            Text.watermarker = watermarker
        }

        const val SOURCE: String = "SupportedFileType"
    }

    class NoFileTypeError(val path: String) : Event.Error(SOURCE) {
        /** Returns a String explaining the event */
        override fun getMessage(): String = "Could not determine file type of $path!"
    }

    class UnsupportedTypeError(val type: String) : Event.Error(SOURCE) {
        /** Returns a String explaining the event */
        override fun getMessage(): String = "Unsupported file type: $type!"
    }
}

@JsExport
open class Watermarker {
    companion object {
        const val SOURCE = "Watermarker"
    }

    private val textWatermarker: TextFileWatermarker = TextFileWatermarker.default()

    /** Watermarks string [text] with [watermark] */
    @JsName("textAddWatermarkBytes")
    fun textAddWatermark(
        text: String,
        watermark: ByteArray,
    ): Result<String> {
        val watermarker = textWatermarker

        val textFile = TextFile.fromString(text)

        val status = watermarker.addWatermark(textFile, watermark)

        return if (status.isError) {
            status.into()
        } else {
            status.into(textFile.content)
        }
    }

    /** Watermarks string [text] with [watermark] */
    fun textAddWatermark(
        text: String,
        watermark: Watermark,
    ): Result<String> {
        val watermarkBytes = watermark.watermarkContent
        return textAddWatermark(text, watermarkBytes)
    }

    /** Watermarks string [text] with [innamarkTagBuilder] */
    @JsName("textAddInnamarkBuilder")
    fun textAddWatermark(
        text: String,
        innamarkTagBuilder: InnamarkTagBuilder,
    ): Result<String> {
        return textAddWatermark(text, innamarkTagBuilder.finish())
    }

    /** Checks if [text] contains a watermark */
    fun textContainsWatermark(text: String): Boolean {
        val textFile = TextFile.fromString(text)
        return textWatermarker.containsWatermark(textFile)
    }

    /**
     * Returns all watermarks in [text].
     *
     * When [squash] is true: watermarks with the same content are merged.
     * When [singleWatermark] is true: only the most frequent watermark is returned.
     */
    fun textGetWatermarks(
        text: String,
        squash: Boolean = true,
        singleWatermark: Boolean = true,
    ): Result<List<Watermark>> {
        val textFile = TextFile.fromString(text)
        return textWatermarker.getWatermarks(textFile, squash, singleWatermark)
    }

    /**
     * Returns all watermarks in [text] as InnamarkTags.
     *
     * When [squash] is true: watermarks with the same content are merged.
     * When [singleWatermark] is true: only the most frequent watermark is returned.
     * When [validateAll] is true: All resulting InnamarkTags are validated to check for errors.
     *
     * Returns a warning if some watermarks could not be converted to InnamarkTags.
     * Returns an error if no watermark could be converted to a InnamarkTag.
     */
    fun textGetInnamarks(
        text: String,
        squash: Boolean = true,
        singleWatermark: Boolean = true,
        validateAll: Boolean = true,
    ): Result<List<InnamarkTag>> {
        val result =
            textGetWatermarks(text, squash, singleWatermark)
                .toInnamarkTags("$SOURCE.textGetInnamarks")
        if (validateAll && result.hasValue && result.value!!.isNotEmpty()) {
            for (innamarkTag in result.value) {
                val validationStatus = innamarkTag.validate()
                result.appendStatus(validationStatus)
            }
        }
        return result
    }

    /**
     * Returns all watermarks in [text] as TextWatermarks.
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
    fun textGetTextWatermarks(
        text: String,
        squash: Boolean = true,
        singleWatermark: Boolean = true,
        errorOnInvalidUTF8: Boolean = false,
    ): Result<List<TextWatermark>> =
        textGetWatermarks(text, squash, singleWatermark)
            .toTextWatermarks(errorOnInvalidUTF8, "$SOURCE.textGetTextWatermarks")

    /** Returns [text] without watermarks */
    fun textRemoveWatermarks(text: String): Result<String> {
        val textFile = TextFile.fromString(text)

        val status = textWatermarker.removeWatermarks(textFile).status

        return status.into(textFile.content)
    }
}

/** Returns [watermarks] without duplicates */
fun <T : Watermark> squashWatermarks(watermarks: List<T>): List<T> {
    return watermarks.toSet().toList()
}
