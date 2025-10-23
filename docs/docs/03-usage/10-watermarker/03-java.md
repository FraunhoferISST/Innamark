---
title: Java
---

<!--
 Copyright (c) 2024 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.

 This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 that can be found in the LICENSE file.
-->

import PatentHint from './../../patent-hint.mdx'

<PatentHint components={props.components} />

# Java
If you want to use watermarking inside your Java project, this page gives you the necessary
information.

## Compile the Library
See [Installation](../installation).

## Example: Watermarking Text with Text
Below you can see an example project that inserts a text as watermark into a cover text and then
extracts the watermark from the watermarked text.

### 1. Add Library as Dependency
*Line 11 is the important line that adds our library as a dependency into the project. Currently, we
are working with local deployment, so you will have to add `mavenLocal()` as repo (line 8) and
publish the library to mavenLocal (see [Installation](../installation)).*
```kt title="build.gradle.kts" showLineNumbers
plugins {
    application
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("de.fraunhofer.isst.innamark:watermarker:0.1.0-SNAPSHOT")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

application {
    mainClass.set("Main")
}
```

### 2. Use the Library
*The static functions `handle(...)` and `unwrap(...)` are optional for easy error handling with our
custom return types (see [Concepts](../../../development/watermarker/concepts/#error-handling-1)
for more details).*

*Watermark extraction can be customized for different use cases using optional Boolean parameters
(see [Watermarker](../#extraction-customization) for more details)*

```java title="src/main/java/Main.java" showLineNumbers
import de.fraunhofer.isst.innamark.watermarker.types.responses.Result;
import de.fraunhofer.isst.innamark.watermarker.types.responses.Status;
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.InnamarkTag;
import de.fraunhofer.isst.innamark.watermarker.watermarkers.text.PlainTextWatermarker;
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.Watermark;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        // *********************
        // ***** INSERTION *****
        // *********************

        // the coverText to be enhanced with a watermark
        String coverText =
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor " +
                        "incididunt ut labore et dolore magna aliqua. Blandit volutpat maecenas " +
                        "volutpat blandit aliquam etiam erat velit.";
        // the watermark text that will be included in the coverText above
        String watermarkText = "Test";

        // prepare watermarker
        PlainTextWatermarker watermarker = new PlainTextWatermarker();

        // inserting the watermark into the cover text and handling potential errors and warnings
        String watermarkedText = unwrap(watermarker.addWatermark(coverText, watermarkText));

        // print the watermarked text
        System.out.println("watermarked text:");
        System.out.println(watermarkedText);
        System.out.println();

        // **********************
        // ***** Extraction *****
        // **********************

        // extract the watermark from the watermarked text
        String extractedWatermark = unwrap(watermarker.getWatermarkAsString(watermarkedText));

        // print the watermark text
        System.out.println("Found a watermark in the text:");
        System.out.println(extractedWatermark);

        // *******************************
        // ***** Multiple watermarks *****
        // *******************************

        // for multiple watermarks in a single text the parameter 'singleWatermark' must be set
        // to 'false' when passed to the extraction function alongside the watermarked text,
        // details are linked above this code block

        // a second Watermark to illustrate multiple watermark extraction
        String secondWatermarkText = "Okay";

        // inserting the second watermark into the coverText
        String secondWatermarkedText = unwrap(watermarker.addWatermark(coverText, secondWatermarkText));

        // combining the watermarked texts to get two different watermarks in one Text
        // note that a corrupt Watermark can be present at the "stitch line" of the input cover texts
        String combinedText = watermarkedText + secondWatermarkedText;

        // extract the watermarks from the watermarked text
        List<Watermark> extractedMultipleWatermarks =
                unwrap(watermarker.getWatermarks(combinedText, false, false));


        // convert Watermark Bytes to Strings
        List<String> extractedMultipleText = new ArrayList<>();
        for (Watermark extracted : extractedMultipleWatermarks) {
            // strip the first (TAG) byte from the extracted Watermarks if they are valid InnamarkTags
            if (InnamarkTag.parse(extracted.getWatermarkContent()).isSuccess()) {
                byte[] watermarkBytes = extracted.getWatermarkContent();
                extractedMultipleText.add(
                        "InnamarkTag found: "+
                                new String(
                                        Arrays.copyOfRange(watermarkBytes, 1, watermarkBytes.length),
                                        StandardCharsets.UTF_8
                                )
                );
            } else {
                extractedMultipleText.add(
                        "Watermark found: "+
                                new String(
                                        extracted.getWatermarkContent(),
                                        StandardCharsets.UTF_8
                                )
                );
            }
        }

        // print the watermarks found
        for (String content : extractedMultipleText) {
            System.out.println(content);
        }

    }

    /**
     * Handles a status depending on its variant:
     * Variant Error:
     *  - print error and exit with code -1
     * Variant Warning:
     *  - print warning
     * Variant Success:
     *  - nop
     */
    public static void handle(Status status) {
        if (status.isSuccess() && !status.hasCustomMessage()) {
            return;
        }

        System.out.println(status);

        if (status.isError()) {
            System.exit(-1);
        }
    }

    /**
     * Unwraps a Result depending on its variant:
     * Variant Error:
     *  - print error and exit with code -1
     * Variant Warning:
     *  - print warning
     *  - return non-null value
     * Variant Success:
     *  - return non-null value
     */
    public static <T> T unwrap(Result<T> result) {
        handle(result.getStatus());
        assert result.hasValue() :
                "A Result with a Status of type Success or Warning is expected to have a value";

        return result.getValue();
    }
}
```
