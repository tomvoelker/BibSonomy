package org.bibsonomy.services.help;

import java.util.Map;

/**
 * factory for {@link HelpParser}
 *
 * @author dzo
 */
public interface HelpParserFactory {
	
	/**
	 * 
	 * @param replacements
	 * @return the {@link HelpParser}
	 */
	public HelpParser createParser(Map<String, String> replacements);
}
