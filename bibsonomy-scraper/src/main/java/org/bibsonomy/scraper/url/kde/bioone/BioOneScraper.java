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
package org.bibsonomy.scraper.url.kde.bioone;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * @author Mohammed Abed
 */
public class BioOneScraper extends AbstractUrlScraper {
	
	private static final String SITE_NAME = "Bio One Research Evolved";
	private static final String SITE_URL = "http://www.bioone.org/";
	private static final String INFO = "This scraper parses a publication page of citations from " + href(SITE_URL, SITE_NAME) + ".";
	private static final List<Pair<Pattern, Pattern>> PATTERNS = new LinkedList<Pair<Pattern, Pattern>>();
	private static final String BIOONE_HOST = "bioone.org";
	private static final String DOWNLOAD_URL = "http://www.bioone.org/action/downloadCitation";
	private static final RisToBibtexConverter ris = new RisToBibtexConverter();
	private static final Pattern DOI_PATTERN_FROM_URL = Pattern.compile("/abs/(.+?)$");
	static {
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*"+ BIOONE_HOST), AbstractUrlScraper.EMPTY_PATTERN));
	}

	@Override
	protected boolean scrapeInternal(ScrapingContext scrapingContext) throws ScrapingException {
		scrapingContext.setScraper(this);
		
		try {
			final String cookie = WebUtils.getCookies(scrapingContext.getUrl());
			String doi = null;
			final Matcher m = DOI_PATTERN_FROM_URL.matcher(scrapingContext.getUrl().toString());
			if (m.find()) {
				doi = "doi=" + m.group(1);
			}
			
			if (doi != null && cookie != null) {
				String resultAsString = null;
				try {
					resultAsString = WebUtils.getPostContentAsString(cookie, new URL(DOWNLOAD_URL), doi);
				} catch (MalformedURLException ex) {
					throw new ScrapingFailureException("URL to scrape does not exist. It may be malformed.");
				}

				final String bibResult = ris.toBibtex(resultAsString);
				if (bibResult != null) {
					scrapingContext.setBibtexResult(bibResult);
					return true;
				}
			}
		} catch (final IOException ex) {
			throw new ScrapingFailureException("An unexpected IO error has occurred. Maybe Bio One is down.");
		}
		return false;
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
