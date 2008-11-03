package org.bibsonomy.scraper;

import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Scrapers of this type can decide using only the URL, if they
 * support the given {@link ScrapingContext} or not. 
 * 
 * @author rja
 * @version $Id$
 */
public interface UrlScraper extends Scraper {
	
	/**
	 * If a scraper does not need to check the path (or host) of a 
	 * the URL, it should return this value as pattern. 
	 */
	public static final Pattern EMPTY_PATTERN = null;
	
	/**
	 * Get a list of patterns the scraper uses to identify supported URLs.
	 * The first pattern of each tuple must match the host part of the URL,
	 * the second pattern must match the path part.    
	 * 
	 * @return A list of supported (host,path)-Patterns. 
	 */
	public List<Tuple<Pattern,Pattern>> getUrlPatterns();
	
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
	public boolean supportsUrl(final URL url);

}
