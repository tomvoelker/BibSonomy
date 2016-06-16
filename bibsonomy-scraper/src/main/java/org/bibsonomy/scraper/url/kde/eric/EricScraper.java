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
package org.bibsonomy.scraper.url.kde.eric;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericRISURLScraper;

/**
 * Scraper for papers from http://www.eric.ed.gov/
 * @author tst
 */
public class EricScraper extends GenericRISURLScraper {

	private static final String SITE_URL = "http://www.eric.ed.gov/";
	private static final String SITE_NAME = "Education Resources Information Center";
	private static final String INFO = "Scraper for publications from the " + href(SITE_URL, SITE_NAME)+".";

	private static final String ERIC_HOST = "eric.ed.gov";

	private static final String EXPORT_BASE_URL = "http://www.eric.ed.gov/ERICWebPortal/MyERIC/clipboard/performExport.jsp?texttype=endnote&accno=";

	private static final Pattern ACCNO_PATTERN = Pattern.compile("accno=([^&]*)");

	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + ERIC_HOST), AbstractUrlScraper.EMPTY_PATTERN));
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.generic.AbstractGenericFormatURLScraper#getDownloadURL(java.net.URL)
	 */
	@Override
	protected String getDownloadURL(URL url, String cookies) throws ScrapingException {
		final Matcher accnoMatcher = ACCNO_PATTERN.matcher(url.getQuery());
		if (accnoMatcher.find()) {
			return EXPORT_BASE_URL + accnoMatcher.group(1);
		}
		
		return null;
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
	
	@Override
	public String getInfo() {
		return INFO;
	}

}
