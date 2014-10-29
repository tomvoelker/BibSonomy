/**
 *
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.scraper.url.kde.rsc;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;

/**
 * @author wla
 */
public class RSCScraper extends GenericBibTeXURLScraper {

	private final Log log = LogFactory.getLog(RSCScraper.class);

	private static final String SITE_NAME = "Royal Society of Chemistry";
	private static final String SITE_URL = "http://pubs.rsc.org";
	private static final String INFO = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);

	private static final String BIBTEX_URL = "http://pubs.rsc.org/en/content/formatedresult?downloadtype=article&managertype=bibtex&markedids=";

	private static final List<Pair<Pattern, Pattern>> PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "pubs.rsc.org"), AbstractUrlScraper.EMPTY_PATTERN));

	private static final Pattern ID_PATTERN = Pattern.compile("(?:/[^/]++)*/(\\w++)");
	private static final int ID_GROUP = 1;


	/**
	 * extracts publication id from url
	 * 
	 * @param url
	 * @return publication id
	 */
	private String extractId(final String url) {
		final Matcher matcher = ID_PATTERN.matcher(url);
		if (matcher.find()) {
			return matcher.group(ID_GROUP);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bibsonomy.scraper.AbstractUrlScraper#getUrlPatterns()
	 */
	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
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
	public String getDownloadURL(URL url) throws ScrapingException {
		final String id = extractId(url.toString());
		if (present(id)) {
			return BIBTEX_URL + id;
		}
		return null;
	}
}
