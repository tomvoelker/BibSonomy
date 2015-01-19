/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
/*
 * Created on 05.09.2006
 */
package org.bibsonomy.scraper;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;

/**
 * Contains all active UrlScrapers. 
 * 
 * @author rja
 *
 */
public class UrlCompositeScraper extends CompositeScraper<UrlScraper> implements UrlScraper {

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		final List<Pair<Pattern, Pattern>> urlPatterns = new LinkedList<Pair<Pattern,Pattern>>();
		for (final Scraper scraper: getScraper()) {
			if (scraper instanceof UrlScraper) {
				urlPatterns.addAll(((UrlScraper)scraper).getUrlPatterns());	
			}
		}
		return urlPatterns;
	}

	@Override
	public boolean supportsUrl(final URL url) {
		for (final Scraper scraper: getScraper()) {
			if (scraper instanceof UrlScraper) {
				if (((UrlScraper)scraper).supportsUrl(url)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @return The name of the site, which gets scraped with this. ATTENTION: UNDEFINED (returns null) FOR COMPOSITE SCRAPER! 
	 * Only defined for concrete URLScraper! 
	 */
	@Override
	public String getSupportedSiteName() {
		return null;
	}

	/**
	 * @return The URL of the site, which gets scraped with this. ATTENTION: UNDEFINED (returns null) FOR COMPOSITE SCRAPER! 
	 * Only defined for concrete URLScraper!
	 */
	@Override
	public String getSupportedSiteURL() {
		return null;
	}
	
}
