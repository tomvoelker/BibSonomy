
package org.bibsonomy.scraper;

import java.util.Collection;

/**
 * Interface for Screen Scrapers.
 */
public interface Scraper {
	
	/** Try to retrieve BibTeX entries from the passed context data.
	 * 
	 * @param scrapingContext - data neccessary for scraping, results of scraping
	 *                          e.g. URL, content of the page, scraping results.
	 * @return True, if scraping was successful and result was stored in context.
	 * @throws ScrapingException if an exception occurs.
	 */
    public boolean scrape(final ScrapingContext scrapingContext) throws ScrapingException;
    
    /** Describe the scraper by a string. 
     * 
     * @return A string to describe the scraper. 
     */
    public String getInfo();
    
    /** A single (leaf) scraper should return Collections.singletonList(this), 
     * a {@link CompositeScraper} a union of its leaf scrapers.
     * 
     * This is used to access scrapers in linear order, for example to print information about them.
     * 
     * @return The current scraper or a list of its subscrapers.
     */
    public Collection<Scraper> getScraper ();

}
