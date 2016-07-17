package org.bibsonomy.webapp.util.markdown;

import java.util.Map;

import org.bibsonomy.services.help.HelpParser;
import org.bibsonomy.services.help.HelpParserFactory;

/**
 * {@link HelpParserFactory} for markdown {@link Parser}
 *
 * @author dzo
 */
public class MarkdownHelpPageParserFactory implements HelpParserFactory {

	/* (non-Javadoc)
	 * @see org.bibsonomy.services.help.HelpParserFactory#createParser(java.util.Map)
	 */
	@Override
	public HelpParser createParser(Map<String, String> replacements) {
		return new Parser(replacements);
	}

}
