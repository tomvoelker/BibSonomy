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
package org.bibsonomy.scraper.url.kde.ams;

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
import org.bibsonomy.scraper.generic.LiteratumScraper;
import org.bibsonomy.util.WebUtils;

/**
 * Scraper for ams.allenpress.com
 * @author tst
 */
public class AmsScraper extends LiteratumScraper implements CitedbyScraper, ReferencesScraper {
	private static final Log log = LogFactory.getLog(AmsScraper.class);
	
	private static final String SITE_NAME = "American Meteorological Society";
	private static final String SITE_HOST = "journals.ametsoc.org";
	private static final String SITE_URL  = "http://" + SITE_HOST + "/";
	private static final String SITE_INFO = "For references from the "+href(SITE_URL, SITE_NAME)+".";
	private static final List<Pair<Pattern, Pattern>> PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + SITE_HOST), AbstractUrlScraper.EMPTY_PATTERN));
	

	private static final Pattern CITEDBY = Pattern.compile("(?s)<a name=\"citedBySection\"></a><h2>Cited by</h2>(.*)<!-- /fulltext content --></div>");
	private static final Pattern REFERENCERS = Pattern.compile("(?s)<table border=\"0\" class=\"references\">(.*)</table><!-- /fulltext content --></div>");
	
	@Override
	public String getInfo() {
		return SITE_INFO;
	}

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
	public boolean scrapeCitedby(ScrapingContext sc) throws ScrapingException {
		try {
			final Matcher m = CITEDBY.matcher(WebUtils.getContentAsString(sc.getUrl().toString().replaceAll("pdf|full", "abs")));
			if (m.find()) {
				sc.setCitedBy(m.group(1));
				return true;
			}
		} catch(Exception e) {
			log.error("error while getting cited by for " + sc.getUrl().toString(), e);
		}
		return false;
	}

	@Override
	public boolean scrapeReferences(ScrapingContext sc) throws ScrapingException {
		try {
			final Matcher m = REFERENCERS.matcher(WebUtils.getContentAsString(sc.getUrl().toString().replaceAll("pdf|abs", "full")));
			if (m.find()) {
				sc.setReferences(m.group(1));
				return true;
			}
		} catch(Exception e) {
			log.error("error while getting references for " + sc.getUrl().toString(), e);
		}
		return false;
	}
}
