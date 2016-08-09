/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.scraper;

import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;

/**
 * @author rja
 */
public interface UrlScraper extends Scraper {

	/**
	 * Get a list of patterns the scraper uses to identify supported URLs.
	 * The first pattern of each tuple must match the host part of the URL,
	 * the second pattern must match the path part.    
	 * 
	 * @return A list of supported (host,path)-Patterns. 
	 */
	public abstract List<Pair<Pattern, Pattern>> getUrlPatterns();

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
	
	/**
	 * @return The name of the site, which gets scraped with this.
	 */
	public abstract String getSupportedSiteName();
	
	/**
	 * @return The URL of the site, which gets scraped with this.
	 */
	public abstract String getSupportedSiteURL();

}