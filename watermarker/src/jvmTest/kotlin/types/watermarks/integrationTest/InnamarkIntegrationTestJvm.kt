/*
 * Copyright (c) 2024-2025 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.
 *
 * This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 * that can be found in the LICENSE file.
 */

package de.fraunhofer.isst.innamark.watermarker.types.watermarks.integrationTest

import de.fraunhofer.isst.innamark.watermarker.types.watermarks.InnamarkTag
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.InnamarkTagInterface
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.memberProperties
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class InnamarkIntegrationTestJvm {
    @Test
    fun innamarkTag_tags_consistentTags() {
        /*
         * This test is designed to verify that all variants of InnamarkTag are using a unique tag.
         * Otherwise, it is not guaranteed that InnamarkTags can be parsed correctly.
         */
        check(InnamarkTagInterface.TAG_SIZE == 1) {
            "Please update the test as the tag type has been modified."
        }

        val classes = InnamarkTag::class.sealedSubclasses // get all variants of InnamarkTag
        val usedTags = Array<Boolean>(256) { false }

        // Iterate over all variants, extract the used tag and verify that it was not used before
        for (c in classes) {
            // get the companion object which should contain the tag
            val companionObjectInstance = c.companionObjectInstance
            assertTrue(
                companionObjectInstance != null,
                "The InnamarkTag class \"${c.simpleName}\" must have a companion object.",
            )

            // get the tag from the companion object
            val tagProperty =
                companionObjectInstance::class.memberProperties.filter { it.name == "TYPE_TAG" }
            assertTrue(
                tagProperty.size == 1,
                "The companion object of the InnamarkTag class \"${c.simpleName}\" must" +
                    "contain a \"const val TAG\".",
            )
            val tag = (tagProperty.first().call(companionObjectInstance) as UByte).toInt()

            // check if the tag was used before
            assertFalse(usedTags[tag], "The tag $tag was used multiple times.")

            // add tag to the list of used tags
            usedTags[tag] = true
        }
    }
}
