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
package org.bibsonomy.scraper.url.kde.jcb;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;
import org.bibsonomy.util.WebUtils;

/**
 * @author hagen
 */
public class JCBScraper extends GenericBibTeXURLScraper {

	private static final String SITE_NAME = "JCB";
	private static final String SITE_URL = "http://jcb.rupress.org/";
	private static final String INFO = "This Scraper parses a publication from " + href(SITE_URL, SITE_NAME)+".";
	
	private static final List<Pair<Pattern,Pattern>> PATTERNS = new ArrayList<Pair<Pattern,Pattern>>();
	
	static {
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile("jcb.rupress.org"), Pattern.compile("content")));
	}
	
	private static final Pattern BIBTEX_PATTERN = Pattern.compile("<a.*href=\"([^\"]+)\".*>BibTeX</a>");

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
	public String getDownloadURL(URL url, String cookies) throws ScrapingException {
		try {
			final String content = WebUtils.getContentAsString(url, cookies);
			final Matcher m = BIBTEX_PATTERN.matcher(content);
			if (m.find()) {
				return SITE_URL + m.group(1);
			}
		} catch (final IOException e) {
			throw new ScrapingException(e);
		}
		return null;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
	}

}
