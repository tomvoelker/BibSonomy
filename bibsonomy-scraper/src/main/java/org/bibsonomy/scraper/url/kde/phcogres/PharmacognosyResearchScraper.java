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
package org.bibsonomy.scraper.url.kde.phcogres;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericRISURLScraper;

/**
 * bibtex download is broken (not correctly formatted) so we use RIS instead
 * 
 * @author hagen
 */
public class PharmacognosyResearchScraper extends GenericRISURLScraper {

	private static final String SITE_NAME = "Pharmacognosy Research";
	private static final String SITE_URL = "http://www.phcogres.com/";
	private static final String INFO = "This Scraper parses a publication from " + href(SITE_URL, SITE_NAME)+".";

	private static final Pattern URL_PATTERN = Pattern.compile(".*/(.*).asp.*");
	
	
	private static final List<Pair<Pattern, Pattern>> URL_PATTERNS = new LinkedList<Pair<Pattern,Pattern>>();
	
	static {
		URL_PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*?" + "www.phcogres.com"), AbstractUrlScraper.EMPTY_PATTERN));
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
		return URL_PATTERNS;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.generic.AbstractGenericFormatURLScraper#getDownloadURL(java.net.URL)
	 */
	@Override
	protected String getDownloadURL(URL url, String cookies) throws ScrapingException {
		String st_url = url.toString();
		Matcher m = URL_PATTERN.matcher(st_url);
		if(m.find()){
			return url.toString().replace(m.group(1), "citeman") + ";t=3";
		}
		return null;
	}
	
	@Override
	protected String postProcessScrapingResult(ScrapingContext scrapingContext, String bibtex) {
		bibtex = bibtex.replaceAll("<i>", "");
		bibtex = bibtex.replaceAll("</i>", "");
		
		return bibtex;
	}
}
