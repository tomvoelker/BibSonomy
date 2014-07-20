package org.bibsonomy.scraper;

import org.bibsonomy.scraper.exceptions.ScrapingException;

/**
 * TODO: add documentation to this class
 *
 * @author Haile
 */
public interface ReferencesScraper {

	 /**
	  * Tries to scrape the references (the publications cited by the given publication). 
	  * Note that this method should only be called <em>after</em> {@link Scraper#scrape(ScrapingContext)}. 
	  * 
	 * @param scrapingContext
	 * @return <code>true</code>, if the cited papers were successfully be scraped
	 * @throws ScrapingException
	 */
	public boolean scrapeReferences(final ScrapingContext scrapingContext) throws ScrapingException;
	
}
