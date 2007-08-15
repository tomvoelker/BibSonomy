
package scraper;

import java.util.Collection;

/**
 * Interface for Screen Scrapers.
 */
public interface Scraper {
	
	/** Try to retrieve bibtex entries from the passed context infos.
	 * @param sc An object that holds important params for scraping, 
	 *           e.g. URL, content or scraping results.
	 * @return True, if scraping was successful and result was stored in context.
	 * @throws ScrapingException if any exception occurs.
	 */
    public boolean scrape (ScrapingContext sc) throws ScrapingException;
    
    /** Describe the scraper by a String. 
     * 
     * @return A String to describe the scraper. 
     */
    public String getInfo();
    
    /** A Single (Leaf) Scraper should return Collections.singletonList(this), 
     * a CompositeScraper a union of its leaf scrapers.
     * 
     * This is used to access scrapers in linear order, for example to print information about them
     * 
     */
    public Collection<Scraper> getScraper ();

}
