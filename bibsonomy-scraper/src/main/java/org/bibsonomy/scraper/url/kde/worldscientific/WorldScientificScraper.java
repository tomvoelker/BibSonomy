/**
 *
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group, University of
 * Kassel, Germany http://www.kde.cs.uni-kassel.de/
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.bibsonomy.scraper.url.kde.worldscientific;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.CitedbyScraper;
import org.bibsonomy.scraper.ReferencesScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;
import org.bibsonomy.util.WebUtils;

/**
 * @author Haile
 */
public class WorldScientificScraper extends GenericBibTeXURLScraper implements CitedbyScraper, ReferencesScraper {

	private static final Log log = LogFactory.getLog(WorldScientificScraper.class);

	private static final String SITE_NAME = "World Scientific";
	private static final String SITE_URL = "http://www.worldscientific.com/";
	private static final String INFO = "This scraper parses a publication page from " + href(SITE_URL, SITE_NAME);
	private static final List<Pair<Pattern, Pattern>> URL_PATTERNS = Collections.singletonList(new Pair<>(Pattern.compile(".*" + "worldscientific.com"), AbstractUrlScraper.EMPTY_PATTERN));
	private static final Pattern ID_PATTERN = Pattern.compile("\\d+.*");
	private static final Pattern REFERENCES_PATTERN = Pattern.compile("(?s)<b>References:</b><ul>(.*)</ul>");
	private static final Pattern CITEDBY_PATTERN = Pattern.compile("(?s)<div class=\"citedByEntry\">(.*)<!-- /fulltext content --></div>");
	private static final Pattern ABSTRACT_PATTERN = Pattern.compile("<div class=\"abstractSection\">(.*)</div><!-- /abstract content -->");

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.UrlScraper#getSupportedSiteName()
	 */
	@Override
	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.UrlScraper#getSupportedSiteURL()
	 */
	@Override
	public String getSupportedSiteURL() {
		return SITE_URL;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.Scraper#getInfo()
	 */
	@Override
	public String getInfo() {
		return INFO;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.AbstractUrlScraper#getUrlPatterns()
	 */
	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return URL_PATTERNS;
	}

	private static String abstractParser(URL url) {
		String st_url = url.toString().replaceAll("ref|pdf|pdfonly", "abs");
		Matcher m;
		try {
			m = ABSTRACT_PATTERN.matcher(WebUtils.getContentAsString(st_url));
			if (m.find()) {
				return m.group(1);
			}
		} catch (IOException e) {
			log.error("error while scraping abstract" + url, e);
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.ReferencesScraper#scrapeReferences(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean scrapeReferences(ScrapingContext scrapingContext) throws ScrapingException {
		try {
			String references = null;
			String st_url = scrapingContext.getUrl().toString().replaceAll("abs|pdf|pdfonly", "ref");
			Matcher m = REFERENCES_PATTERN.matcher(WebUtils.getContentAsString(st_url));
			if (m.find()) {
				references = m.group(1);
			}
			if (references != null) {
				scrapingContext.setReferences(references);
				return true;
			}
		} catch (IOException e) {
			log.error("error while scraping references " + scrapingContext.getUrl(), e);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.CitedbyScraper#scrapeCitedby(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean scrapeCitedby(ScrapingContext scrapingContext) throws ScrapingException {
		try {
			String citedby = null;
			String st_url = scrapingContext.getUrl().toString().replaceAll("ref|pdf|pdfonly", "abs");
			Matcher m = CITEDBY_PATTERN.matcher(WebUtils.getContentAsString(st_url));
			if (m.find()) {
				citedby = m.group(1);
			}
			if (citedby != null) {
				scrapingContext.setCitedBy(citedby);
				return true;
			}
		} catch (IOException e) {
			log.error("error while scraping cited by " + scrapingContext.getUrl(), e);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.generic.AbstractGenericFormatURLScraper#getDownloadURL(java.net.URL)
	 */
	@Override
	protected String getDownloadURL(URL url) throws ScrapingException {
		String id = null;
		String bibtex_url = "http://" + url.getHost() + "/action/downloadCitation?format=bibtex&doi=";

		Matcher m = ID_PATTERN.matcher(url.toString());
		if (m.find()) {
			id = m.group();
		}

		if (id == null) {
			return null;
		}

		return bibtex_url + id;
	}

	@Override
	protected String postProcessScrapingResult(ScrapingContext scrapingContext, String bibtex) {
		return BibTexUtils.addFieldIfNotContained(bibtex, "abstract", abstractParser(scrapingContext.getUrl()));
	}
}
