package org.bibsonomy.scraper;

import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author rja
 * @version $Id$
 */
public interface UrlScraper extends Scraper {

	/**
	 * Get a list of patterns the scraper uses to identify supported URLs.
	 * The first pattern of each tuple must match the host part of the URL,
	 * the second pattern must match the path part.    
	 * 
	 * @return A list of supported (host,path)-Patterns. 
	 */
	public abstract List<Tuple<Pattern, Pattern>> getUrlPatterns();

	/** Checks if this scraper supports the given URL.
	 * <p>
	 * Note that UrlScrapers {@link Scraper#supportsScrapingContext(ScrapingContext)}
	 * method must delegate to {@link #supportsUrl(URL)} by calling
	 * <code>supportsUrl(scrapingContext.getUrl())</code>!
	 * </p>
	 * 
	 * @param url
	 * @return <code>true</code> if the scraper can extract metadata from
	 * the given URL.
	 */
	public abstract boolean supportsUrl(final URL url);

}