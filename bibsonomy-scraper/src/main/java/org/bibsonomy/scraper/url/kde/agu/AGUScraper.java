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
package org.bibsonomy.scraper.url.kde.agu;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericRISURLScraper;

/**
 * Scraper for publications from http://www.agu.org/pubs/ using the RIS export
 * 
 * @author tst
 */
public class AGUScraper extends GenericRISURLScraper {
	private static final String SITE_NAME = "American Geophysical Union (AGU)";
	private static final String SITE_URL = "http://www.agu.org/pubs/";
	private static final String INFO = "For Publications from the " + href(SITE_URL, SITE_NAME)+".";

	private static final String HOST = "agu.org";

	private final Pattern RIS_DOWNLOAD_PATTERN = Pattern.compile("href=\"([^\\\"]*)\">Export RIS Citation");

	private static final List<Pair<Pattern, Pattern>> patterns = new ArrayList<Pair<Pattern,Pattern>>();

	static {
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + HOST), EMPTY_PATTERN));
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}
	@Override
	public String getInfo() {
		return INFO;
	}

	@Override
	public String getSupportedSiteName() {
		return "AGU";
	}

	@Override
	public String getSupportedSiteURL() {
		return SITE_URL;
	}

	@Override
	protected String getDownloadURL(URL url) {
		try {
			final ScrapingContext sc = new ScrapingContext(url);
			final String pageContent = sc.getPageContent();
			if (pageContent != null) {
				Matcher matcherDownloadUrl = RIS_DOWNLOAD_PATTERN.matcher(pageContent);
				if (matcherDownloadUrl.find()){
					return "http://www.agu.org" + matcherDownloadUrl.group(1).replace("&amp;", "&");
				}
			}
		} catch (ScrapingException e) {
			// ignore
		}
		
		return null;
	}
}
