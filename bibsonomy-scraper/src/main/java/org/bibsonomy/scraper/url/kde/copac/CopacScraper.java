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
package org.bibsonomy.scraper.url.kde.copac;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * this Scraper import data from the host http://copac.jisc.ac.uk
 * @author Mohammed Abed
 */
public class CopacScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "Copac National, Academic and Specialist Library Catalogue";
	private static final String SITE_URL = "http://copac.jisc.ac.uk/";
	private static final String info = "This scraper parses a publication page of citations from " + href(SITE_URL, SITE_NAME) + ".";

	private static final String COPAC_HOST = "http://copac.jisc.ac.uk/";
	private static final String HOST = "jisc.ac.uk";
	private static final String EXPORT_BIBTEX = "&format=BibTeX&action=Export";
	private static final List<Pair<Pattern, Pattern>> patterns = new LinkedList<Pair<Pattern, Pattern>>();
	
	static {
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + HOST), Pattern.compile("/search?")));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + COPAC_HOST), AbstractUrlScraper.EMPTY_PATTERN));
	}

	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		String cookie = null;
		try {
			final String bibResult = WebUtils.getContentAsString(cookie, sc.getUrl().toString() + EXPORT_BIBTEX );
			System.out.println(bibResult);
		} catch (IOException e) {
			throw new ScrapingFailureException("URL to scrape does not exist. It maybe malformed.");
		} 
		// System.out.println(sc.getUrl().toString() + EXPORT_BIBTEX);
		return false;
	}
	
	//@Override
	protected String getDownloadURLTest(URL url) throws ScrapingException {
		return SITE_URL + "/search?";
		//return "http://copac.jisc.ac.uk/search?title=Measures+and+aggregation%3A+formal+aspects+and+applications+to+clustering+and+decision.&rn=1&format=BibTeX&action=Export";
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
