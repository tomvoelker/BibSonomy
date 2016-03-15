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
package org.bibsonomy.scraper.url.kde.googlescholar;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;

/**
 * This scraper supports download links from the GoogleSonomy Firefox plugin.
 * 
 * @author tst
 */
public class GoogleScholarScraper extends GenericBibTeXURLScraper {
	
	private static final String SITE_URL  = "http://scholar.google.com/";
	private static final String SITE_NAME = "Google Scholar";
	private static final String INFO = "Scrapes BibTex from " + href(SITE_URL, SITE_NAME) + ".";
	private static final String HOST = "scholar.google.";
	private static final String PATH1 = "/scholar.bib";
	private static final String PATH2 = "/citations";
	private static final Pattern ID = Pattern.compile("citation_for_view=(.+?)$");
	private static final List<Pair<Pattern, Pattern>> patterns = new LinkedList<Pair<Pattern, Pattern>>();
	static {
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + HOST + ".*"), Pattern.compile(PATH1 + ".*")));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + HOST + ".*"), Pattern.compile(PATH2 + ".*")));
	}
	
	@Override
	protected String getDownloadURL(URL url) throws ScrapingException {
		/* 
		if (true) { //citation_for_view=(.+?)$
			final Pattern IDFORGOOGLE = Pattern.compile("(.+?)scisig(.+?)$", Pattern.DOTALL);
			Matcher m = null;
			try {
				System.out.println(WebUtils.getContentAsString(url));
				m = IDFORGOOGLE.matcher(WebUtils.getContentAsString(url));
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (m.find()) {
				final String id = m.group(1);
				System.out.println("ssss" + id);
				//return url.toString().replace("view_citation", "export_citations") + "&s=" + id + "&cit_fmt=0";
			}
		}*/
		
		final Matcher m = ID.matcher(url.toString());
		if (m.find()) {
			final String id = m.group(1);
			return url.toString().replace("view_citation", "export_citations") + "&s=" + id + "&cit_fmt=0";
		}
		
		return url.toString();
	}

	@Override
	public String getInfo() {
		return INFO;
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
}
