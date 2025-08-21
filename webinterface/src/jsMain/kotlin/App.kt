/*
 * Copyright (c) 2023-2025 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.
 *
 * This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 * that can be found in the LICENSE file.
 */

import io.kvision.Application
import io.kvision.BootstrapCssModule
import io.kvision.BootstrapModule
import io.kvision.BootstrapUploadModule
import io.kvision.CoreModule
import io.kvision.FontAwesomeModule
import io.kvision.Hot
import io.kvision.html.image
import io.kvision.html.span
import io.kvision.i18n.I18n.tr
import io.kvision.panel.root
import io.kvision.panel.tab
import io.kvision.panel.tabPanel
import io.kvision.panel.vPanel
import io.kvision.startApplication
import io.kvision.utils.auto
import io.kvision.utils.em
import io.kvision.utils.perc
import io.kvision.utils.useModule

@JsModule("/kotlin/modules/css/custom.css")
@JsNonModule
external val CustomCss: dynamic

@JsModule("/kotlin/modules/img/innamark_logo_b.svg")
@JsNonModule
external val InnamarkLogo: dynamic

class App : Application() {
    init {
        useModule(CustomCss)
    }

    /** Initial method to load the default watermarker form */
    override fun start() {
        root("innamark") {
            vPanel {
                // Styling
                width = 90.perc
                marginLeft = auto
                marginRight = auto
                paddingTop = 1.em

                // Logo + intro text
                image(InnamarkLogo, alt = "Innamark logo", responsive = true) {
                    width = 10.em
                    marginBottom = 1.em
                }
                span(
                    "This tool allows to hide or reveal a text-based watermark " +
                        "(word, name, sentence, etc.) in a text of your choice.",
                ) {
                    marginBottom = 1.em
                }

                tabPanel {
                    tab(tr("Embed"), "fas fa-file-import", route = "/watermarkEmbed") {
                        add(WatermarkTextEmbedTab())
                    }
                    tab(tr("Extract"), "fas fa-file-export", route = "/watermarkExtract") {
                        add(WatermarkTextExtractTab())
                    }
                }
            }
        }
    }
}

/** Main KVision method to load modules */
fun main() {
    startApplication(
        ::App,
        js("import.meta.webpackHot").unsafeCast<Hot?>(),
        BootstrapModule,
        BootstrapCssModule,
        BootstrapUploadModule,
        CoreModule,
        FontAwesomeModule,
    )
}
