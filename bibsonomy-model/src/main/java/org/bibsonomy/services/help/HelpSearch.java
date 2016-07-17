package org.bibsonomy.services.help;

import java.util.SortedSet;

/**
 * interface for the help search
 *
 * @author dzo
 */
public interface HelpSearch {
	
	/**
	 * search in help
	 * @param language
	 * @param searchTerms
	 * @return the page results
	 */
	public SortedSet<HelpSearchResult> search(final String language, final String searchTerms);

}
