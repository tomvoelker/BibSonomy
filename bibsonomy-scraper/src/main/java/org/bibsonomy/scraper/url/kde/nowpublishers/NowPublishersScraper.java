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
package org.bibsonomy.scraper.url.kde.nowpublishers;

import static org.bibsonomy.util.ValidationUtils.present;

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
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.WebUtils;

/**
 * Scrapes publications from NowPublishers.
 * 
 * @author Mohammed Abed
 */
public class NowPublishersScraper extends AbstractUrlScraper {
	private static final String SITE_NAME = "Now The essence of Knowledge";
	private static final String SITE_URL = "http://www.nowpublishers.com";
	private static final String INFO 	= "Scrapes publications from " + href(SITE_URL, SITE_NAME);
	
	private static final URL DOWNLOAD_URL;
	private static final String DOWNLOAD_PATH = "/article/ExportCitation";
	
	private static final String NOWPUBLISHERS_HOST = "nowpublishers.com";
	private static final String NOWPUBLISHERS_URL_PATH_START = "/article/Details/";
	
	private static final Pattern ID_URL_PATTERN = Pattern.compile(".*" + NOWPUBLISHERS_HOST + NOWPUBLISHERS_URL_PATH_START + "(.*)");
	
	private static final List<Pair<Pattern, Pattern>> patterns = new LinkedList<Pair<Pattern,Pattern>>();
	static{
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + NOWPUBLISHERS_HOST), Pattern.compile(NOWPUBLISHERS_URL_PATH_START + ".*")));
		try {
			DOWNLOAD_URL = new URL(SITE_URL + DOWNLOAD_PATH);
		} catch (final MalformedURLException e) {
			throw new RuntimeException(e);
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
		return patterns;
	}
	
	@Override
	protected boolean scrapeInternal(final ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		
		final String urlAsString = sc.getUrl().toString();
		final Matcher idURLMatcher = ID_URL_PATTERN.matcher(urlAsString);
		if (!idURLMatcher.find()) {
			throw new ScrapingFailureException("Can't get acticle id for scraping bibtex for " + urlAsString);
		}
		
		final String pubIdUrl = idURLMatcher.group(1);
		final String postArgs = "id=" + pubIdUrl + "&format=BIB";
		
		try {
			final String bibtex = WebUtils.getPostContentAsString(DOWNLOAD_URL, postArgs, StringUtils.CHARSET_UTF_8);
			if (present(bibtex)) {
				sc.setBibtexResult(bibtex.trim());
				return true;
			}
		} catch (final MalformedURLException ex) {
			throw new ScrapingFailureException("URL to scrape does not exist. It maybe malformed.");
		} catch (final IOException ex) {
			throw new ScrapingFailureException("An unexpected IO error has occurred. Maybe NowPublishers is down.");
		}
		
		return false;
	}
}
