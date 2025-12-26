package org.bibsonomy.api.search;

import org.bibsonomy.model.Document;

/**
 * Lightweight no-op extractor to keep the search bean graph intact without
 * pulling legacy file handling dependencies.
 */
public class MinimalFileContentExtractorService implements org.bibsonomy.search.index.utils.FileContentExtractorService {
    /**
     * Extracts content from a document. This no-op implementation always returns
     * an empty string regardless of input.
     *
     * @param document the document to extract content from (may be null)
     * @return always returns an empty string; never returns null
     */
    @Override
    public String extractContent(final Document document) {
        return "";
    }
}
