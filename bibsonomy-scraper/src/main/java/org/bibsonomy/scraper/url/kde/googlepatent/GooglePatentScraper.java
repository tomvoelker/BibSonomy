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
package org.bibsonomy.scraper.url.kde.googlepatent;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;

/**
 * @author Mohammed Abed
 */
public class GooglePatentScraper extends GenericBibTeXURLScraper {
	private static final String SITE_NAME = "Google Patente";
	private static final String SITE_URL = "http://www.google.com/patents";
	private static final String INFO = "This scraper parses a publication page of citations from " + href(SITE_URL, SITE_NAME) + ".";
	private static final String GOOGLE_PATENT_HOST = "google.com";
	private static final Pattern URL_PATTERN = Pattern.compile("(.*)?\\?(.*)$");
	private static final List<Pair<Pattern, Pattern>> patterns = new LinkedList<Pair<Pattern, Pattern>>();
	static {
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*"+ GOOGLE_PATENT_HOST), Pattern.compile("/patents/")));
	}

	@Override
	protected String getDownloadURL(URL url, String cookies) throws ScrapingException, IOException {
		final Matcher m = URL_PATTERN.matcher(url.toString());
		if (m.find()) {
			return m.group(1) + ".bibtex" + "?" + m.group(2);
		} 
		throw new ScrapingFailureException("failure getting bibtex url for " + url);
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
		return patterns;
	}
}
