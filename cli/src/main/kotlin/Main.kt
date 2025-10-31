/*
 * Copyright (c) 2023-2025 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.
 *
 * This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 * that can be found in the LICENSE file.
 */

import de.fraunhofer.isst.innamark.watermarker.types.responses.Event
import de.fraunhofer.isst.innamark.watermarker.types.responses.Result
import de.fraunhofer.isst.innamark.watermarker.types.responses.Status
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.RawInnamarkTag
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.Watermark
import de.fraunhofer.isst.innamark.watermarker.utils.SupportedFileType
import de.fraunhofer.isst.innamark.watermarker.utils.getFileType
import de.fraunhofer.isst.innamark.watermarker.watermarkers.file.TextFileWatermarker
import de.fraunhofer.isst.innamark.watermarker.watermarkers.file.ZipFileWatermarker
import de.fraunhofer.isst.innamark.watermarker.watermarkers.text.PlainTextWatermarker
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import kotlinx.cli.default
import kotlinx.cli.optional
import kotlin.math.min
import kotlin.system.exitProcess

val watermarker = PlainTextWatermarker()
val textFileWatermarker = TextFileWatermarker()
val zipFileWatermarker = ZipFileWatermarker

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        cli(arrayOf("-h"))
    } else {
        cli(args)
    }
}

/**
 * Parses the CLI arguments and calls the corresponding functions
 * Prints usage information if the CLI arguments cannot be parsed
 */
@OptIn(ExperimentalCli::class)
fun cli(args: Array<String>) {
    val parser = ArgParser("watermarker", strictSubcommandOptionsOrder = true)

    val fileType by parser.option(ArgType.String, "file-type", "t", "Specify the file type")
    val verbose by parser.option(ArgType.Boolean, "verbose", "v", "Verbose Output").default(false)

    class Add : Subcommand("add", "Add watermark to a file") {
        val message by argument(
            ArgType.String,
            fullName = "message",
            description = "Watermark message",
        )
        val source by argument(ArgType.String, fullName = "source", description = "Source file")
        val target by argument(
            ArgType.String,
            fullName = "target",
            description = "Target file - if omitted <source> gets overwritten",
        ).optional()

        override fun execute() = add(message, source, target, fileType)
    }

    class List : Subcommand("list", "List watermarks in a file") {
        val source by argument(ArgType.String, fullName = "source", description = "Source file")

        override fun execute() {
            list(source, fileType, verbose)
        }
    }

    class Remove : Subcommand("remove", "Removes all watermarks from a file") {
        val source by argument(ArgType.String, fullName = "source", description = "Source file")
        val target by argument(
            ArgType.String,
            fullName = "target",
            description = "Target file - if omitted <source> gets overwritten",
        ).optional()

        override fun execute() = remove(source, target, fileType)
    }

    class TextAdd : Subcommand("add", "Add watermark to a string") {
        val text by argument(
            ArgType.String,
            fullName = "text",
            description = "String to watermark",
        )
        val message by argument(
            ArgType.String,
            fullName = "message",
            description = "Watermark message",
        )

        override fun execute() {
            textAdd(text, message)
        }
    }

    class TextList : Subcommand("list", "List watermarks in a string") {
        val text by argument(
            ArgType.String,
            fullName = "text",
            description = "String containing a watermark",
        )

        override fun execute() {
            textList(text, verbose)
        }
    }

    class TextRemove : Subcommand("remove", "Removes all watermarks from a string") {
        val text by argument(
            ArgType.String,
            fullName = "text",
            description = "String containing watermark(s)",
        )

        override fun execute() {
            textRemove(text)
        }
    }

    class Text : Subcommand("text", "Watermark text directly") {
        init {
            this.subcommands(TextAdd())
            this.subcommands(TextList())
            this.subcommands(TextRemove())
        }

        override fun execute() {
            if (args.size == 1) {
                cli(arrayOf("text", "-h"))
            }
        }
    }

    parser.subcommands(Add(), List(), Remove(), Text())
    parser.parse(args)
}

/**
 * Adds a watermark containing [message] to file [source]
 * Changes are written to [target] or [source] if target is null
 */
fun add(
    message: String,
    source: String,
    target: String?,
    fileType: String?,
) {
    val realTarget = target ?: source
    val sourceType = determineType(source, fileType)
    val watermark = RawInnamarkTag.fromString(message)
    val status = Status.success()

    if (sourceType.equals("text", ignoreCase = true)) {
        status.appendStatus(textFileWatermarker.addWatermark(source, realTarget, watermark))
    } else if (sourceType.equals("zip", ignoreCase = true)) {
        status.appendStatus(zipFileWatermarker.addWatermark(source, realTarget, watermark))
    } else {
        status.appendStatus(SupportedFileType.NoFileTypeError(source).into())
    }

    status.handle()

    println()
    println("Added watermark to ${getTargetHint(source, target)}")
}

/**
 * Prints a list of all watermarks in [source]
 *
 * Uses watermark squashing when [verbose] is false
 */
fun list(
    source: String,
    fileType: String?,
    verbose: Boolean,
) {
    val sourceType = determineType(source, fileType)

    val watermarks: Result<List<Watermark>> =
        if (sourceType.equals("text", ignoreCase = true)) {
            textFileWatermarker.getWatermarks(
                source,
                fileType,
                squash = !verbose,
                singleWatermark = false,
            )
        } else if (sourceType.equals("zip", ignoreCase = true)) {
            zipFileWatermarker.getWatermarks(
                source,
                fileType,
                squash = !verbose,
                singleWatermark = false,
            )
        } else {
            Result(
                SupportedFileType.NoFileTypeError(source).into(),
                listOf(Watermark.fromString("placeholder")),
            )
        }

    val realWatermarks = watermarks.unwrap()

    println()
    println("Found ${realWatermarks.size} watermark(s) in '$source':")
    printWatermarks(realWatermarks)
}

/**
 * Removes all watermarks from [source] and prints them
 * Changes are written to [target] or [source] if target is null
 *
 * Handles the Results of parsing, removing and writing
 */
fun remove(
    source: String,
    target: String?,
    fileType: String?,
) {
    val realTarget = target ?: source
    val sourceType = determineType(source, fileType)

    val status =
        if (sourceType.equals("text", ignoreCase = true)) {
            textFileWatermarker.removeWatermarks(source, realTarget)
        } else if (sourceType.equals("zip", ignoreCase = true)) {
            zipFileWatermarker.removeWatermarks(source, realTarget)
        } else {
            SupportedFileType.NoFileTypeError(source).into()
        }

    status.handle()

    println()
    println("Removed all watermarks from ${getTargetHint(source, target)}:")
}

/** Adds a watermark containing [message] to [text] and prints the resulting string */
fun textAdd(
    text: String,
    message: String,
) {
    val watermarkedText = watermarker.addWatermark(text, message).unwrap()
    println("-- Watermarked Text " + "-".repeat(60))
    println(watermarkedText)
    println("-".repeat(80))
}

/**
 * Prints a list of all watermarks in [text]
 *
 * Uses watermark squashing when [verbose] is false
 */
fun textList(
    text: String,
    verbose: Boolean,
) {
    val watermarks = watermarker.getWatermarks(text, !verbose, false).unwrap()
    println()
    println("Found ${watermarks.size} watermark(s):")
    printWatermarks(watermarks)
}

/** Removes all watermarks from [text] and prints the resulting string */
fun textRemove(text: String) {
    if (watermarker.containsWatermark(text)) {
        val cleaned = watermarker.removeWatermarks(text).unwrap()
        println("-- Cleaned Text " + "-".repeat(60))
        println(cleaned)
        println("-".repeat(80))
    } else {
        println("Unable to find any watermarks in the text.")
    }
}

/**
 * returns a String that indicates if changes where written to
 *  - [target]: if [target] is not null
 *  - [source]: else
 */
fun getTargetHint(
    source: String,
    target: String?,
): String =
    if (target != null) {
        "$source and wrote changes to $target"
    } else {
        source
    }

/** Prints each watermark in [watermarks] with a separator between each one */
fun printWatermarks(watermarks: List<Watermark>) {
    val indexStringLen = min(watermarks.size.toString().length, 70)

    for ((index, watermark) in watermarks.withIndex()) {
        print("-- %${indexStringLen}d ".format(index + 1))
        println("-".repeat(80 - indexStringLen - 4))
        println("'${watermark.watermarkContent.decodeToString()}'")
    }

    if (watermarks.isNotEmpty()) println("-".repeat(80))
}

/** Prints each event to STDOUT or STDERR using the toString Method if type is not SUCCESS */
fun Status.print() {
    if (this.getEvents().isEmpty()) {
        when {
            this.isSuccess -> println("Success")
            this.isWarning -> System.err.println("Warning")
            this.isError -> System.err.println("Error")
        }
    } else {
        for (event in this.getEvents()) {
            when (event) {
                is Event.Success -> println(event)
                is Event.Warning, is Event.Error -> System.err.println(event)
            }
        }
    }
}

/**
 * Handles a status depending on its variant:
 *
 * Variant Error:
 *  - print error and exit with code -1
 *
 * Variant Warning:
 *  - print warning
 *
 * Variant Success:
 *  - nop
 */
fun Status.handle() {
    if (isSuccess && !hasCustomMessage) {
        return
    }

    this.print()

    if (this.isError) {
        exitProcess(-1)
    }
}

/**
 * Unwraps a Result depending on its variant:
 *
 * Variant Error:
 *  - print error and exit with code -1
 *
 * Variant Warning:
 *  - print warning
 *  - return non-null value
 *
 * Variant Success:
 *  - return non-null value
 */
fun <T> Result<T>.unwrap(): T {
    this.status.handle()
    checkNotNull(value) {
        "A Result with a Status of type Success or Warning are expected to have a value"
    }

    return value!!
}

/**
 * Determines the file Type (as a String) from [fileType] if not null, else from the [source] path.
 */
private fun determineType(
    source: String,
    fileType: String?,
): String {
    return if (fileType != null) {
        fileType
    } else {
        val type = SupportedFileType.getFileType(source, null)
        when (type.value) {
            SupportedFileType.Text -> "Text"
            SupportedFileType.Zip -> "Zip"
            else -> "Unknown"
        }
    }
}
