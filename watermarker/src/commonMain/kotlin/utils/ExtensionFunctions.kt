/*
 * Copyright (c) 2023-2025 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.
 *
 * This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 * that can be found in the LICENSE file.
 */

package de.fraunhofer.isst.innamark.watermarker.utils

/**
 * Returns a UInt created from 4 bytes in little endian order
 * If less than 4 bytes are supplied they get filled with zeros from MSB to LSB
 */
fun UInt.Companion.fromBytesLittleEndian(bytes: List<Byte>): UInt {
    require(bytes.size <= 4)
    var result = 0u
    for ((index, byte) in bytes.withIndex()) {
        result = result or ((byte.toUInt() and 255u) shl (index * 8))
    }
    return result
}

/**
 * Returns a UShort created from 2 bytes in little endian order
 * If less than 2 bytes are supplied they get filled with zeros from MSB to LSB
 */
fun UShort.Companion.fromBytesLittleEndian(bytes: List<Byte>): UShort {
    require(bytes.size <= 2)
    var result = 0u
    for ((index, byte) in bytes.withIndex()) {
        result = result or ((byte.toUInt() and 255u) shl (index * 8))
    }
    return result.toUShort()
}

/** Converts a UInt into a list of 4 bytes in little endian order */
fun UInt.toBytesLittleEndian(): List<Byte> =
    listOf(
        this.toByte(),
        this.shr(8).toByte(),
        this.shr(16).toByte(),
        this.shr(24).toByte(),
    )

/** Converts a UShort into a list of 2 bytes in little endian order */
fun UShort.toBytesLittleEndian(): List<Byte> =
    listOf(
        this.toByte(),
        this.toUInt().shr(8).toByte(),
    )

/** Coverts a Byte into an UByte and returns it as Int */
fun Byte.toIntUnsigned(): Int = this.toInt() and 0xff

/** Returns the Unicode representation of the character */
fun Char.toUnicodeRepresentation(): String =
    "\\u" + this.code.toString(16).uppercase().padStart(4, '0')

/**
 * Represents a ByteArray in hex
 * E.g.: [0xde, 0xad, 0xbe, 0xef]
 */
fun ByteArray.toHexString(): String =
    "[" +
        joinToString(separator = ", ") { eachByte ->
            eachByte.toIntUnsigned().toString(16).uppercase().padStart(2, '0')
        } +
        "]"
