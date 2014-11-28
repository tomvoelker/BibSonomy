/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.scraper.url.kde.morganclaypool;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;

/**
 * @author Haile
 */
public class MorganClaypoolScraper extends GenericBibTeXURLScraper {
	private static final String SITE_NAME = "Morgan & Claypool Publisher";
	private static final String SITE_URL = "http://www.morganclaypool.com/";
	private static final String INFO = "This Scraper parses a publication from "
			+ href(SITE_URL, SITE_NAME) + ".";

	private static final Pattern BIBTEX_DOI = Pattern.compile("[abs/|doi=](\\d.*)");
	private static final String BIBTEX_PATH = "/action/downloadCitation/showCitFormats?doi=";
	private static final List<Pair<Pattern, Pattern>> URL_PATTERNS = new ArrayList<Pair<Pattern, Pattern>>();

	static {
		URL_PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*?"
				+ "www.morganclaypool.com"), AbstractUrlScraper.EMPTY_PATTERN));
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

	@Override
	public String getDownloadURL(URL url) throws ScrapingException {
		final Matcher m = BIBTEX_DOI.matcher(url.toString());
		if (m.find()) {
			final String DOI = m.group(1);
			return "http://" + url.getHost().toString() + BIBTEX_PATH + DOI + "&format=bibtex";
		}
		
		return null;
	}

}
