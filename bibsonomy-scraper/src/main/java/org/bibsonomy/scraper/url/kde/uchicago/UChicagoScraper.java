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
package org.bibsonomy.scraper.url.kde.uchicago;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * @author Mohammed Abed
 */
public class UChicagoScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "The University of Chicago Press Journals";
	private static final String SITE_URL = "http://www.journals.uchicago.edu/";
	private static final String INFO = "Supports journals from " + href(SITE_URL, SITE_NAME);
	private static final String CHICAGO_HOST = "journals.uchicago.edu";

	private static final String DOWNLOAD_PATH_AND_QUERY = "/action/downloadCitation?format=bibtex&include=abs&doi=";

	private static final Pattern doiPattern = Pattern.compile("/doi/(?:(?:abs)|(?:full))/(.*)");
	private static final Pattern doiPatternQuery = Pattern.compile("doi=([^&]*)");

	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + CHICAGO_HOST), AbstractUrlScraper.EMPTY_PATTERN));
	private final Pattern URL_PATTERN_FOR_URL = Pattern.compile("URL = \\{ \n        (.*)\n    \n\\}");
	
	@Override
	protected boolean scrapeInternal(final ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);

		String doi = null;
		String bibtex = null;

		// get doi from path
		Matcher doiMatcher = doiPattern.matcher(sc.getUrl().getPath());
		if (doiMatcher.find()) {
			doi = doiMatcher.group(1);
		}

		// check if doi is in path
		if (doi != null) {
			bibtex = download(doi);
		} else {

			// get doi from query

			doiMatcher = doiPatternQuery.matcher(sc.getUrl().getQuery());
			if (doiMatcher.find()) {
				doi = doiMatcher.group(1);
			}

			if (doi != null) {
				bibtex = download(doi);
			} else {
				throw new PageNotSupportedException("This page journals.uchicago.edu is not supported.");
			}
		}

		if (bibtex != null) { 
			Matcher m = URL_PATTERN_FOR_URL.matcher(bibtex);
			if(m.find()) {
				bibtex = bibtex.replaceAll(URL_PATTERN_FOR_URL.toString(), "URL = {" + m.group(1) + "}");
				sc.setBibtexResult(bibtex);
				return true;
			}
		} else {
			throw new ScrapingFailureException("Bibtex download failed. Can't scrape any bibtex.");
		}
		return false;
	}

	@Override
	public String getInfo() {
		return INFO;
	}

	/**
	 * Get a bibtex reference by its doi from journals.uchicago.edu
	 * 
	 * @param doi
	 * @return reference as bibtex
	 * @throws ScrapingException
	 */
	private static String download(final String doi) throws ScrapingException {
		String bibtex = null;
		final String downloadUrl = "http://" + CHICAGO_HOST + DOWNLOAD_PATH_AND_QUERY + doi;

		try {
			bibtex = WebUtils.getContentAsString(downloadUrl);
		} catch (final IOException ex) {
			throw new InternalFailureException(ex);
		}

		return bibtex;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

	@Override
	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	@Override
	public String getSupportedSiteURL() {
		return SITE_URL;
	}
}
