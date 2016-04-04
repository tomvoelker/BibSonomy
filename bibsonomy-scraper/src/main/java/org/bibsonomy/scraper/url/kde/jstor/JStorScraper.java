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
package org.bibsonomy.scraper.url.kde.jstor;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;
import org.bibsonomy.util.WebUtils;

/**
 * @author wbi
 */
public class JStorScraper extends GenericBibTeXURLScraper {

	private static final String info = "This Scraper parses a publication from " + href("http://www.jstor.org/", "JSTOR");
	private static final String SITE_NAME = "Jstor";
	private static final String JSTOR_HOST  = "jstor.org";
	private static final String JSTOR_HOST_NAME  = "http://www.jstor.org";
	private static final String JSTOR_STABLE_PATH = "/stable/";
	private static final String DOWNLOAD_URL = "http://www.jstor.org/citation/text/";	
	private static final List<Pair<Pattern,Pattern>> patterns = new LinkedList<Pair<Pattern,Pattern>>();
	private static final Pattern DOI = Pattern.compile("class=\"button button-jstor cite-this-item\" data-reveal-id=\"citation-tools\" data-doi=\"(.*?)\"");
	
	static {
		final Pattern hostPattern = Pattern.compile(".*" + JSTOR_HOST);
		patterns.add(new Pair<Pattern, Pattern>(hostPattern, Pattern.compile(JSTOR_STABLE_PATH + ".*")));
	}
	
	@Override
	protected String getDownloadURL(URL url) throws ScrapingException, IOException {
		final String doi = exportDOIFromSourceCode(url.toString());
		if(doi != null) {
			return DOWNLOAD_URL + doi;
		}
		return null;
	}

	private static String exportDOIFromSourceCode(String url) throws ScrapingException {
		try {
			final Matcher m = DOI.matcher(WebUtils.getContentAsString(url));
			if(m.find()) {
				return m.group(1);
			}
			} catch(IOException e) {
				throw new ScrapingException("DOI not found");
			}
		return null;
	}

	@Override
	public String getInfo() {
		return info;
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
		return JSTOR_HOST_NAME;
	}	
}
