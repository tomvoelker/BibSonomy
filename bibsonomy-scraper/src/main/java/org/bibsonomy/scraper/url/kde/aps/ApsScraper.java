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
package org.bibsonomy.scraper.url.kde.aps;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;
import org.bibsonomy.util.WebUtils;

/**
 * @author Haile
 */
public class ApsScraper extends GenericBibTeXURLScraper{
	private static final String SITE_NAME = "American Psychological Society";
	private static final String SITE_URL = "http://the-aps.org";
	private static final String INFO = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);
	//private static final String BIBTEX_URL = "citmgr?type=bibtex&gca=";
	private static final List<Pair<Pattern, Pattern>> PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "physiology.org"), AbstractUrlScraper.EMPTY_PATTERN));

	//private static final Pattern URL_PATTERN = Pattern.compile("(http://[^/]++)(\\W+)");
	//private static final Pattern URL_START = Pattern.compile("/\\w+");
	//private static final Pattern ID_PATTERN = Pattern.compile("(\\d+\\W)+");
	
	private static final Pattern BIBTEX_PATTERN = Pattern.compile("<li class=\"bibtext first\"><a href=\"(.*)\">BibTeX</a></li>");
	private static final Pattern EXTRADATA = Pattern.compile("\" class=\"\" data-icon-position=\"\" data-hide-link-title=\"0");
	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
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
	public String getDownloadURL(final URL url) throws ScrapingException {
		try {
			final Matcher m = BIBTEX_PATTERN.matcher(WebUtils.getContentAsString(url));
			String download_link = "";
			if (m.find()) {
				download_link = m.group(1);
				download_link = download_link.replaceAll(EXTRADATA.toString(), " ");
			} else {
				throw new ScrapingFailureException("failure getting bibtex url for " + url);
			}
			
			return "http://" + url.getHost().toString() + download_link;
		} catch (IOException e) {
			throw new ScrapingException(e);
		}
	}
	@Override
	protected String convert(String result){
		final String firstLine = result.split("\n")[0].trim();
		if(firstLine.split("\\{").length == 1)
			result = result.replace(firstLine,firstLine.replace("{", "{nokey,"));
		return result;
	}
}
