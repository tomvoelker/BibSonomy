package org.bibsonomy.scraper;

import org.bibsonomy.scraper.exceptions.ScrapingException;

/**
 * TODO: add documentation to this class
 *
 * @author Haile
 */
public interface CitedbyScraper {

	 /**
	  * Tries to scrape the publications that cite the given publication.
	  * Note that this method should only be called <em>after</em> {@link Scraper#scrape(ScrapingContext)}.
	  * 
	 * @param scrapingContext
	 * @return <code>true</code>, if the citing papers were successfully be scraped
	 * @throws ScrapingException
	 */
	public boolean scrapeCitedby(final ScrapingContext scrapingContext) throws ScrapingException;
	
}
