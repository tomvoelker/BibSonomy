package org.bibsonomy.scraper;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.exceptions.ScrapingException;

/**
 * Scrapers of this type can decide using only the URL, if they
 * support the given {@link ScrapingContext} or not. 
 * 
 * @author rja
 * @version $Id$
 */
public abstract class UrlScraper implements Scraper {

	/**
	 * If a scraper does not need to check the path (or host) of a 
	 * the URL, it should return this value as pattern. 
	 */
	protected static final Pattern EMPTY_PATTERN = null;

	
	/**
	 * Get a list of patterns the scraper uses to identify supported URLs.
	 * The first pattern of each tuple must match the host part of the URL,
	 * the second pattern must match the path part.    
	 * 
	 * @return A list of supported (host,path)-Patterns. 
	 */
	public abstract List<Tuple<Pattern,Pattern>> getUrlPatterns();

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
	public boolean supportsUrl(final URL url) {
		if (url != null) {
			final List<Tuple<Pattern, Pattern>> urlPatterns = getUrlPatterns();;

			/*
			 * possible matching combinations:
			 * first = true && second = true
			 * first = true && second = null
			 * first = null && second = true
			 */
			for (final Tuple<Pattern, Pattern> tuple: urlPatterns){
				final boolean match1 = tuple.getFirst() == EMPTY_PATTERN ||
				tuple.getFirst().matcher(url.getHost()).find();

				final boolean match2 = tuple.getSecond() == EMPTY_PATTERN || 
				tuple.getSecond().matcher(url.getPath()).find();

				if (match1 && match2) return true;

			}
		}
		return false;
	}

	/** Builds a href to the URL with the given anchor text.
	 *  
	 * @param url
	 * @param text
	 * @return
	 */
	protected static String href(final String url, final String text) {
		return "<a href=\"" + url + "\">" + text + "</a>";
	}

	/** Scrapes the given context.
	 * 
	 * @see org.bibsonomy.scraper.Scraper#scrape(org.bibsonomy.scraper.ScrapingContext)
	 */
	public boolean scrape(final ScrapingContext sc) throws ScrapingException {
		if (sc != null && supportsUrl(sc.getUrl())) {
			return scrapeInternal(sc);
		}
		return false;
	}
	
	
	/** This method is called by {@link #scrape(ScrapingContext)}, when the URL is supported
	 * by the scraper.
	 *  
	 * @param scrapingContext
	 * @return <code>true</code> if the scraping was successful.
	 * @throws ScrapingException
	 */
	protected abstract boolean scrapeInternal(final ScrapingContext scrapingContext) throws ScrapingException;

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}
	
	public boolean supportsScrapingContext(ScrapingContext scrapingContext) {
		return supportsUrl(scrapingContext.getUrl());
	}

}
