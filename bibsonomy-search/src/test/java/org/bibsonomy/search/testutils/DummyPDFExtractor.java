package org.bibsonomy.search.testutils;

import java.io.File;
import java.io.IOException;

import org.bibsonomy.search.index.utils.extractor.ContentExtractor;

/**
 * dummy pdf extractor for tests
 *
 * @author dzo
 */
public class DummyPDFExtractor implements ContentExtractor {

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.index.utils.extractor.ContentExtractor#supports(java.lang.String)
	 */
	@Override
	public boolean supports(String fileName) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.index.utils.extractor.ContentExtractor#extractContent(java.io.File)
	 */
	@Override
	public String extractContent(File file) throws IOException {
		return null;
	}

}
