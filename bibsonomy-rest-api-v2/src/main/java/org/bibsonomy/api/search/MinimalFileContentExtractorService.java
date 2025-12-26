package org.bibsonomy.api.search;

import org.bibsonomy.model.Document;

/**
 * Lightweight no-op extractor to keep the search bean graph intact without
 * pulling legacy file handling dependencies.
 */
public class MinimalFileContentExtractorService implements org.bibsonomy.search.index.utils.FileContentExtractorService {
    @Override
    public String extractContent(final Document document) {
        return "";
    }
}
