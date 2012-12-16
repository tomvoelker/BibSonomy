package org.bibsonomy.scraper.generic;

import java.io.IOException;
import java.net.URL;

import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import static org.bibsonomy.util.ValidationUtils.present;
import org.bibsonomy.util.WebUtils;

/**
 * Super class to support pattern "URL in -> URL out".
 * 
 * @author hagen
 * @version $Id$
 */
public abstract class SimpleGenericURLScraper extends AbstractUrlScraper {

	/**
	 * Implementations of this class should return the download link for the BibTeX file.
	 * 
	 * @param url The URL to be scraped.
	 * @return The URL that points to the download.
	 */
	public abstract String getBibTeXURL(final URL url);
	
	@Override
	protected boolean scrapeInternal(ScrapingContext scrapingContext) throws ScrapingException {
		scrapingContext.setScraper(this);
		try {
			String bibtexURL = getBibTeXURL(scrapingContext.getUrl());
			String bibtexResult = WebUtils.getContentAsString(bibtexURL);
			
			if (present(bibtexResult)) {
				scrapingContext.setBibtexResult(bibtexResult);
				return true;
			}
		} catch (IOException e) {
			throw new ScrapingException(e);
		}
		return false;
	}

}
