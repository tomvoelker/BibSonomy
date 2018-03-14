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
package org.bibsonomy.scraper.url.kde.worldscientific;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.CitedbyScraper;
import org.bibsonomy.scraper.ReferencesScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.ExamplePrototype;
import org.bibsonomy.scraper.generic.LiteratumScraper;
import org.bibsonomy.util.WebUtils;

/**
 * @author Haile
 */
public class WorldScientificScraper extends LiteratumScraper implements CitedbyScraper, ReferencesScraper, ExamplePrototype {

	private static final Log log = LogFactory.getLog(WorldScientificScraper.class);

	private static final String SITE_NAME = "World Scientific";
	private static final String SITE_HOST = "worldscientific.com";
	private static final String SITE_URL  = "http://" + SITE_HOST + "/";
	private static final String SITE_INFO = "This scraper parses a publication page from " + href(SITE_URL, SITE_NAME);
	private static final List<Pair<Pattern, Pattern>> URL_PATTERNS = Collections.singletonList(new Pair<>(Pattern.compile(".*" + SITE_HOST), AbstractUrlScraper.EMPTY_PATTERN));

	private static final Pattern REFERENCES_PATTERN = Pattern.compile("(?s)<b>References:</b><ul>(.*)</ul>");
	private static final Pattern CITEDBY_PATTERN = Pattern.compile("(?s)<div class=\"citedByEntry\">(.*)<!-- /fulltext content --></div>");

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
		return URL_PATTERNS;
	}
	@Override
	protected String postProcessBibtex(ScrapingContext sc, String bibtex) {
		// removed XML remains (found in journal title of test case)
		return bibtex.replace("&amp;", "\\&");
	}


	@Override
	public boolean scrapeReferences(ScrapingContext scrapingContext) throws ScrapingException {
		try {
			final String st_url = scrapingContext.getUrl().toString().replaceAll("abs|pdf|pdfonly", "ref");
			final Matcher m = REFERENCES_PATTERN.matcher(WebUtils.getContentAsString(st_url));
			if (m.find()) {
				scrapingContext.setReferences(m.group(1));
				return true;
			}
		} catch (IOException e) {
			log.error("error while scraping references " + scrapingContext.getUrl(), e);
		}
		return false;
	}

	@Override
	public boolean scrapeCitedby(ScrapingContext scrapingContext) throws ScrapingException {
		try {
			final String st_url = scrapingContext.getUrl().toString().replaceAll("ref|pdf|pdfonly", "abs");
			final Matcher m = CITEDBY_PATTERN.matcher(WebUtils.getContentAsString(st_url));
			if (m.find()) {
				scrapingContext.setCitedBy(m.group(1));
				return true;
			}
		} catch (IOException e) {
			log.error("error while scraping cited by " + scrapingContext.getUrl(), e);
		}
		return false;
	}

}
