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
package org.bibsonomy.scraper.url.kde.apha;

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
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * @author Mohammed Abed
 */
public class APHAScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "American Journal of PUBLIC HEALTH";
	private static final String SITE_URL = "http://ajph.aphapublications.org/";
	private static final String info = "This scraper parses a publication page of citations from " + href(SITE_URL, SITE_NAME) + ".";
	
	private static final Pattern DOI_PATTERN_FROM_URL = Pattern.compile("/abs/(.+?)$");
	private static final String DOWNLOAD_URL = "http://ajph.aphapublications.org/action/downloadCitation";
	private static final String HOST = "aphapublications.org";
	private static final String AJPH_HOST = "ajph.aphapublications.org";
	
	private static final List<Pair<Pattern, Pattern>> patterns = new LinkedList<Pair<Pattern, Pattern>>();
	static {
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + HOST), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + AJPH_HOST), AbstractUrlScraper.EMPTY_PATTERN));
	}
	
	private final RisToBibtexConverter ris = new RisToBibtexConverter();

	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		String cookie = null;
		String doi = null;
		
		try {
			cookie = WebUtils.getCookies(sc.getUrl());
		} catch (final IOException ex) {
			throw new InternalFailureException("An unexpected IO error has occurred. No Cookie has been generated.");
		}
		
		final Matcher m = DOI_PATTERN_FROM_URL.matcher(sc.getUrl().toString());
		if (m.find()) {
			doi = "doi=" + m.group(1);
		}
		
		String risString = null;
		try {
			risString = WebUtils.getPostContentAsString(cookie, new URL(DOWNLOAD_URL), doi);
		} catch (MalformedURLException ex) {
			throw new ScrapingFailureException("URL to scrape does not exist. It maybe malformed.");
		} catch (IOException ex) {
			throw new ScrapingFailureException("An unexpected IO error has occurred. Maybe APHA Publications is down.");
		}
		
		String bibResult = ris.risToBibtex(risString);
		if (bibResult != null) {
			sc.setBibtexResult(bibResult);
			return true;
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
		return info;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}
}
