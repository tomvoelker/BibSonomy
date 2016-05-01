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
package org.bibsonomy.scraper.url.kde.ahajournals;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;

/**
 * @author Mohammed Abed
 */
public class AhaJournalsScraper extends GenericBibTeXURLScraper{

	private static final String SITE_NAME = "Aha Journals";
	private static final String SITE_URL = "http://circ.ahajournals.org/";
	private static final String info = "This scraper parses a publication page of citations from " + href(SITE_URL, SITE_NAME) + ".";
	private static final String AHA_JOURNALS_HOST = "circ.ahajournals.org";
	private static final List<Pair<Pattern, Pattern>> patterns = new LinkedList<Pair<Pattern, Pattern>>();
	static {
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + AHA_JOURNALS_HOST), AbstractUrlScraper.EMPTY_PATTERN));
	}
	private static final Pattern PATTERN_FROM_URL_1 = Pattern.compile(".*/(CIRCULATIONAHA.*).abstract");
	private static final Pattern PATTERN_FROM_URL_2 = Pattern.compile(".*/content/(.*).([0-9]).abstract");
	private static final String DOWNLOAD_URL = "http://circ.ahajournals.org/citmgr?type=bibtex&gca=circulationaha%3B";
	
	@Override
	protected String getDownloadURL(URL url) throws ScrapingException, IOException {
		
		final String id = extractID(url);
		if (id == null)
			return null;
		return DOWNLOAD_URL + id;
	}
	
	private String extractID(URL url) {
		Matcher m = PATTERN_FROM_URL_1.matcher(url.toString());
		if(m.find()) {
			return m.group(1);
		}
		m = PATTERN_FROM_URL_2.matcher(url.toString());
		if(m.find()) {
			return m.group(1);
		}
		return null;
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
