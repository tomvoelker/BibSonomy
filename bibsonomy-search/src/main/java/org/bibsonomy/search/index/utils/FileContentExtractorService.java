package org.bibsonomy.search.index.utils;

import org.bibsonomy.model.Document;

/**
 * interface for file content extraction
 *
 * @author dzo
 */
public interface FileContentExtractorService {

	/**
	 * @param document
	 * @return the content of the document
	 */
	public String extractContent(Document document);

}
