package org.bibsonomy.api.search

import org.bibsonomy.search.index.utils.extractor.ContentExtractor
import java.io.File

/**
 * Minimal PDF extractor placeholder.
 *
 * The original webapp wires a full PDF extractor that depends on PDFBox and
 * servlet-only wiring. To keep the search stack bootable in the standalone
 * REST API we provide a lightweight extractor that advertises PDF support but
 * returns empty content. This keeps the bean graph intact without pulling the
 * heavyweight web stack.
 */
class NoopPdfExtractor : ContentExtractor {
    override fun supports(fileName: String?): Boolean {
        return fileName?.lowercase()?.endsWith(".pdf") ?: false
    }

    override fun extractContent(file: File?): String = ""
}
