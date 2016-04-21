package org.bibsonomy.search.index.utils.extractor;

import java.io.File;
import java.io.IOException;

/**
 * extractor for content
 *
 * @author dzo
 */
public interface ContentExtractor {
	
	/**
	 * @param fileName
	 * @return <code>true</code> if the file can be extracted by this implementation
	 */
	public boolean supports(final String fileName);
	
	/**
	 * @param file
	 * @return the extracted content of the provided file
	 * @throws IOException
	 */
	public String extractContent(final File file) throws IOException;
}
