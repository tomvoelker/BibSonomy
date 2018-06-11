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
package org.bibsonomy.scraper.url.kde.base;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;

/**
 * Scraper for BASE (https://www.base-search.net/)
 *
 * @author rja
 */
public class BaseScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "BASE";
	private static final String SITE_HOST = "base-search.net";
	private static final String SITE_URL  = "https://www." + SITE_HOST + "/";
	private static final String INFO = "This scraper extracts publication metadata from " + href(SITE_URL, SITE_NAME) + ".";
	private static final List<Pair<Pattern, Pattern>> PATTERNS = new LinkedList<Pair<Pattern, Pattern>>();
	static {
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + SITE_HOST), Pattern.compile("/Record/.*")));
	}

	@Override
	protected boolean scrapeInternal(final ScrapingContext sc) throws ScrapingException {
		try {
			sc.setScraper(this);
			final URL url = sc.getUrl();
			// build query URL
			final String path = url.getPath();
			final String queryUrl;
			if (path.endsWith("/Export")) {
				queryUrl = url.toString();
			} else {
				queryUrl = url.toString() + "/Export";
			}
			// get data with post request
			final String bibtex = WebUtils.getContentAsString(queryUrl, null, "style[]=BibTeX", null);

			sc.setBibtexResult(bibtex);
			return true;
		} catch (final IOException ex) {
			throw new InternalFailureException(ex);
		}
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
