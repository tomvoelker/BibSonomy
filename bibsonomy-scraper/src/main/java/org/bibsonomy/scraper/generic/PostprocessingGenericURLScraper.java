package org.bibsonomy.scraper.generic;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;

/**
 * TODO: add documentation to this class
 *
 * @author Haile
 */
/**
 * Allows {@link SimpleGenericURLScraper}s to postprocess the scraped result, i.e., by converting it to BibTeX or modifying the BibTeX.
 * 
 * @author Haile
 *
 */

public abstract class PostprocessingGenericURLScraper extends SimpleGenericURLScraper {


	@Override
	protected boolean scrapeInternal(ScrapingContext scrapingContext) throws ScrapingException {
		final boolean result = super.scrapeInternal(scrapingContext);
		if (result) {
			scrapingContext.setBibtexResult(this.postProcessScrapingResult(scrapingContext, scrapingContext.getBibtexResult()));
		}
		return result;
	}
	
	protected abstract String postProcessScrapingResult(ScrapingContext sc,final String result);

}
