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
package org.bibsonomy.scraper.url.kde.osti;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;

/**
 * @author Mohammed Abed
 */
public class OstiScraper extends AbstractUrlScraper{

	private static final String SITE_NAME = "U.S. Departement of energy - Office of Scientific and technical information";
	private static final String SITE_URL = "http://osti.gov";
	private static final String INFO = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);
	private static final List<Pair<Pattern, Pattern>> PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "osti.gov"), AbstractUrlScraper.EMPTY_PATTERN));

	@Override
	protected boolean scrapeInternal(ScrapingContext scrapingContext) throws ScrapingException {
		scrapingContext.setScraper(this);
		final Pattern PATTERN_GETTING_DOWNLOAD_PATH = Pattern.compile("(.*/\\d+)");
		Matcher m = PATTERN_GETTING_DOWNLOAD_PATH.matcher(scrapingContext.getUrl().toString());
		try {
			if (m.find()) {
				final String cookie = WebUtils.getCookies(scrapingContext.getUrl());
				String expectedBibtex = WebUtils.getContentAsString(new URL(m.group(1)+"/cite/bibtex"), cookie);
				final String bibtexResult = removeHTML(expectedBibtex);
				scrapingContext.setBibtexResult(bibtexResult);
				return true;
			}
		} catch (IOException e) {
			throw new InternalFailureException(e);
		}
		return false;
	}
	/*
	 * clean the bibtex from html code
	 */
	private String removeHTML(String expectedBibtex) {
		String bibtexResult = expectedBibtex.replace("  <div class=\"csl-entry\"> ", "");
		bibtexResult.replace("</div>", "");
		return bibtexResult;
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
