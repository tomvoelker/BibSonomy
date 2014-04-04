package org.bibsonomy.scraper.generic;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.net.URL;

import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;

/**
 * Transforms the scraping result from RIS to BibTeX after scraping, using the {@link RisToBibtexConverter}.
 * 
 * 
 * @author Haile
 *
 */
public abstract class RISGenericURLScraper extends AbstractUrlScraper {
	public abstract String getRISURL(final URL url);
	private static RisToBibtexConverter RIS2BIB = new RisToBibtexConverter();
	@Override
	protected boolean scrapeInternal(ScrapingContext scrapingContext) throws ScrapingException {
		scrapingContext.setScraper(this);
		try {
			String bibtexURL = getRISURL(scrapingContext.getUrl());
			String bibtexResult = WebUtils.getContentAsString(bibtexURL);
			final String bibtex = RIS2BIB.risToBibtex(bibtexResult);
			if (present(bibtex)) {
				scrapingContext.setBibtexResult(bibtex);
				return true;
			}
		} catch (IOException e) {
			throw new ScrapingException(e);
		}
		return false;
	}
}
