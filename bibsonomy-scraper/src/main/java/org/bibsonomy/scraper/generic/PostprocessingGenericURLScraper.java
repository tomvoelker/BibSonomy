package org.bibsonomy.scraper.generic;

import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;

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
			scrapingContext.setBibtexResult(this.postProcessScrapingResult(scrapingContext.getBibtexResult()));
		}
		return result;
	}
	
	protected abstract String postProcessScrapingResult(final String result);

}
