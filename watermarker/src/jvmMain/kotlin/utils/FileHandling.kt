/*
 * Copyright (c) 2023-2025 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.
 *
 * This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 * that can be found in the LICENSE file.
 */

package de.fraunhofer.isst.innamark.watermarker.utils

import de.fraunhofer.isst.innamark.watermarker.types.files.WatermarkableFile
import de.fraunhofer.isst.innamark.watermarker.types.responses.Event
import de.fraunhofer.isst.innamark.watermarker.types.responses.Result
import de.fraunhofer.isst.innamark.watermarker.watermarkers.file.FileWatermarker
import de.fraunhofer.isst.innamark.watermarker.watermarkers.file.TextFileWatermarker
import de.fraunhofer.isst.innamark.watermarker.watermarkers.file.ZipFileWatermarker
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.extension

class FileHandling {
    companion object {
        fun readFile(path: String): Result<ByteArray> =
            try {
                Result.success(File(path).readBytes())
            } catch (e: Exception) {
                WatermarkableFile.ReadError(path, e.message ?: e.stackTraceToString()).into<_>()
            }
    }
}

sealed class SupportedFileType {
    abstract val watermarker: FileWatermarker<*>

    object Text : SupportedFileType() {
        override var watermarker: TextFileWatermarker = TextFileWatermarker()
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

    class NoFileTypeError(
        val path: String,
    ) : Event.Error(SOURCE) {
        /** Returns a String explaining the event */
        override fun getMessage(): String = "Could not determine file type of $path!"
    }

    class UnsupportedTypeError(
        val type: String,
    ) : Event.Error(SOURCE) {
        /** Returns a String explaining the event */
        override fun getMessage(): String = "Unsupported file type: $type!"
    }

    class WrongTypeError(
        val type: String,
        val errorSource: String,
    ) : Event.Error(errorSource) {
        /** Returns a String explaining the event */
        override fun getMessage(): String = "Unsupported file type for chosen Watermarker: $type!"
    }
}

/**
 * Parses the file type from [fileType] or if it is null from [path]'s extension.
 * Returns an error if the file type cannot be determined.
 */
fun SupportedFileType.Companion.getFileType(
    path: String,
    fileType: String?,
): Result<SupportedFileType> {
    val parsedFileType =
        if (fileType != null) {
            fileType
        } else {
            val parsedPath = Path(path)
            if (parsedPath.extension == "") return SupportedFileType.NoFileTypeError(path).into<_>()
            parsedPath.extension
        }

    return fromExtension(parsedFileType)
}
