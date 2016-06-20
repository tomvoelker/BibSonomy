/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package org.bibsonomy.scraper.url.kde.jamanet;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;

/**
 * @author Mohammed Abed
 */
public class JamaNetScraper extends GenericBibTeXURLScraper {
	
	private static final String SITE_NAME = "The Journal of American Medical Association";
	private static final String HOST = "jama.jamanetwork.com";
	private static final String SITE_URL = "http://" + HOST + "/";
	private static final String INFO = "This scraper parses a publication page of citations from " + href(SITE_URL, SITE_NAME) + ".";
	private static final List<Pair<Pattern, Pattern>> PATTERNS = new LinkedList<Pair<Pattern, Pattern>>();
	private static final String DOWNLOAD_URL = SITE_URL + "downloadCitation.aspx?format=bibtex&articleid=";
	private static final Pattern ID_PATERN_FROM_URL = Pattern.compile(".+?articleid=(.+)$");
	static {
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + HOST), AbstractUrlScraper.EMPTY_PATTERN));
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.generic.AbstractGenericFormatURLScraper#getDownloadURL(java.net.URL, java.lang.String)
	 */
	@Override
	protected String getDownloadURL(URL url, String cookies) throws ScrapingException, IOException {
		final Matcher m = ID_PATERN_FROM_URL.matcher(url.toString());
		if (m.find()) {
			final String id = m.group(1);
			return DOWNLOAD_URL + id;
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.generic.AbstractGenericFormatURLScraper#postProcessScrapingResult(org.bibsonomy.scraper.ScrapingContext, java.lang.String)
	 */
	@Override
	protected String postProcessScrapingResult(ScrapingContext scrapingContext, String bibtex) {
		return bibtex.replace(" + ", "");
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
		return INFO;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
	}
}
