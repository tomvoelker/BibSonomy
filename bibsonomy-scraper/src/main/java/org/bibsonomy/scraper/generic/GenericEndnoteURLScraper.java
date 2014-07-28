package org.bibsonomy.scraper.generic;

import org.bibsonomy.scraper.converter.EndnoteToBibtexConverter;

/**
 * 
 * @author dzo
 */
public abstract class GenericEndnoteURLScraper extends AbstractGenericFormatURLScraper {
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.generic.AbstractGenericFormatURLScraper#convert(java.lang.String)
	 */
	@Override
	protected String convert(String downloadResult) {
		// TODO: thread save?
		final EndnoteToBibtexConverter converter = new EndnoteToBibtexConverter();
		return converter.endnoteToBibtex(downloadResult);
	}
}
