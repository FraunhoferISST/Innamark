/*
 * Copyright (c) 2023-2025 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.
 *
 * This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 * that can be found in the LICENSE file.
 */

package de.fraunhofer.isst.innamark.watermarker.utils

import de.fraunhofer.isst.innamark.watermarker.types.responses.Result

@JsModule("pako")
@JsNonModule
external object Pako {
    /*
     * Both functions should receive UByteArray but Kotlin/JS contains a bug that compiles an
     * UByteArray to Int8Array instead of an UInt8Array (in JS) which crashes Pako. The workaround
     * is using an IntArray which gets compiled to an Int32Array and all UBytes and be represented
     * as positive numbers.
     *
     * Issue:
     * https://youtrack.jetbrains.com/issue/KT-58867/Unsigned-arrays-get-compiled-to-singed-arrays
     */

    /**
     * Compresses [data] using the deflate algorithm with [options] (see
     * https://nodejs.org/api/zlib.html#class-options)
     */
    fun deflateRaw(
        data: IntArray,
        options: Any? = definedExternally,
    ): IntArray

    /**
     * Uncompresses [data] using the inflate algorithm with [options] (see
     * https://nodejs.org/api/zlib.html#class-options)
     */
    fun inflateRaw(
        data: IntArray,
        options: Any? = definedExternally,
    ): IntArray
}

object DeflateOptions {
    @JsName("level")
    val level: Int = COMPRESSION_LEVEL
}

@JsExport
actual object Compression {
    /** Compresses [data] using the deflate algorithm */
    actual fun inflate(data: ByteArray): Result<ByteArray> {
        // Fixing different behavior on empty lists
        if (data.isEmpty()) return Result.success(byteArrayOf())

        /*
         * BUG: An UByteArray get compiled to Int8Array in JS and contains negative numbers
         * This causes Pako to crash when inflating
         * https://youtrack.jetbrains.com/issue/KT-58867/Unsigned-arrays-get-compiled-to-singed-arrays
         *
         * Workaround: Convert Byte to Int and apply byte mask to prevent negative numbers
         */
        val workaroundData = data.map { it.toInt() and 0xff }.toIntArray()
        return try {
            val bytes = Pako.inflateRaw(workaroundData).map { it.toByte() }
            Result.success(bytes.toByteArray())
        } catch (e: dynamic) {
            InflationError(e.toString()).into<_>()
        }
    }

    /** Uncompresses [data] using the inflate algorithm */
    actual fun deflate(data: ByteArray): ByteArray {
        // Fixing different behavior on empty lists
        if (data.isEmpty()) return byteArrayOf()

        /*
         * BUG: An UByteArray get compiled to Int8Array in JS and contains negative numbers
         * This causes Pako to crash when inflating
         * https://youtrack.jetbrains.com/issue/KT-58867/Unsigned-arrays-get-compiled-to-singed-arrays
         *
         * Workaround: Convert Byte to Int and apply byte mask to prevent negative numbers
         */
        val workaroundData = data.map { it.toInt() and 0xff }.toIntArray()
        return Pako.deflateRaw(workaroundData, DeflateOptions).map { it.toByte() }.toByteArray()
    }
}
