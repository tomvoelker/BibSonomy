package org.bibsonomy.services.help;

import java.io.IOException;

/**
 * a parser for the help pages
 *
 * @author dzo
 */
public interface HelpParser {
	
	/**
	 * @param fileName
	 * @return the parsed content
	 * @throws IOException
	 */
	public String parseFile(final String fileName) throws IOException;
}
