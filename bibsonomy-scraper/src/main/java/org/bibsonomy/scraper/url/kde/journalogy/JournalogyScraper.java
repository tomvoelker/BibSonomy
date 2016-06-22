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
package org.bibsonomy.scraper.url.kde.journalogy;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;

/**
 * Scraper for Journalogy (Microsoft Academic Search)
 * http://www.journalogy.org
 * 
 * @author clemens
 */
public class JournalogyScraper extends GenericBibTeXURLScraper {
	private static final Log log = LogFactory.getLog(JournalogyScraper.class);
	
	private static final String SITE_NAME = "Journalogy (Microsoft Academic Search)";
	private static final String SITE_URL = "http://www.journalogy.org/";
	private static final String info = "This scraper parses a publication page of citations from "
			+ href(SITE_URL, SITE_NAME)+".";
	
	private static final String HOST = "journalogy.org";
	private static final String HOST2 = "academic.research.microsoft.com";

	private static final List<Pair<Pattern, Pattern>> patterns = new LinkedList<Pair<Pattern, Pattern>>();
	static {
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + HOST), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + HOST2), AbstractUrlScraper.EMPTY_PATTERN));
	}
	
	private static final Pattern pattern_download = Pattern.compile(".bib?type=2&format=0&download=1");
	private static final Pattern pattern_id = Pattern.compile("/(Paper|Publication)/([0-9]+)");
	
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
	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.generic.PostprocessingGenericURLScraper#postProcessScrapingResult(org.bibsonomy.scraper.ScrapingContext, java.lang.String)
	 */
	@Override
	protected String postProcessScrapingResult(ScrapingContext sc, String result) {
		return result.replace("{{", "{").replace("}}", "}");
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.generic.SimpleGenericURLScraper#getBibTeXURL(java.net.URL)
	 */
	@Override
	public String getDownloadURL(URL url, String cookies) throws ScrapingException {
		try {
			// extract id
			final Matcher idMatcher = pattern_id.matcher(url.toString());

			if(idMatcher.find()) {
				return "http://" + HOST2 + "/" + idMatcher.group(2) + pattern_download;
			} 
		} catch (Exception ex) {
			log.error("error while getting download url for " + url, ex);
		}
		return null;
	}
}
