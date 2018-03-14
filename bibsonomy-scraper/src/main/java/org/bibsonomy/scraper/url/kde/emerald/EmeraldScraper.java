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
package org.bibsonomy.scraper.url.kde.emerald;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.generic.LiteratumScraper;
import org.bibsonomy.util.UrlUtils;

/**
 * This scraper supports download links from emeraldinsight.com
 * 
 * FIXME: currently does not work, as the server sends a 302 redirect response for the 
 * POST request and the HttpClient does not support following redirects for POST requests.
 * Needs to be handled by manually implementing the request handling using the HttpClient
 * directly 
 * 
 * @author Mohammed Abed
 */
public class EmeraldScraper extends LiteratumScraper {
	private static final String SITE_NAME = "Emerald Publishing";
	private static final String SITE_HOST = "emeraldinsight.com";
	private static final String SITE_URL  = "http://" + SITE_HOST + "/";
	private static final String SITE_INFO = "This scraper parses a publication page of citations from " + href(SITE_URL, SITE_NAME) + ".";

	private static final List<Pair<Pattern, Pattern>> PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*"+ SITE_HOST), AbstractUrlScraper.EMPTY_PATTERN));

	@Override
	protected String getPostContent(String doi) {
		// include=abs&format=bibtex&direct=on&doi=
		return "format=bibtex&include=abs&doi=" + UrlUtils.safeURIEncode(doi);
	}
	
	@Override
	protected boolean requiresCookie() {
		return true;
	}
		@Override
	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	@Override
	public String getSupportedSiteURL() {
		return SITE_URL;
	}

	@Override
	public String getInfo() {
		return SITE_INFO;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
	}

}
