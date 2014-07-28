package org.bibsonomy.scraper.generic;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.net.URL;

import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * @author dzo
 */
public abstract class AbstractGenericFormatURLScraper extends AbstractUrlScraper {
	
	protected abstract String getDownloadURL(final URL url) throws ScrapingException;
	
	@Override
	protected final boolean scrapeInternal(ScrapingContext scrapingContext) throws ScrapingException {
		scrapingContext.setScraper(this);
		try {
			final URL url = scrapingContext.getUrl();
			final String downloadURL = getDownloadURL(url);
			if (downloadURL == null) {
				throw new ScrapingFailureException("can't get download url for " + url);
			}
			final String downloadResult = WebUtils.getContentAsString(downloadURL);
			
			String bibtex = this.convert(downloadResult);
			
			if (present(bibtex)) {
				bibtex = postProcessScrapingResult(scrapingContext, bibtex);
				scrapingContext.setBibtexResult(bibtex);
				return true;
			}
		} catch (final IOException e) {
			throw new ScrapingException(e);
		}
		return false;
	}
	
	/**
	 * @param scrapingContext
	 * @param bibtex
	 * @return the postProcessed bibtex
	 */
	protected String postProcessScrapingResult(ScrapingContext scrapingContext, String bibtex) {
		return bibtex;
	}

	/**
	 * @param downloadResult
	 * @return downloadResult, converted to bibtex
	 */
	protected abstract String convert(String downloadResult);
}
