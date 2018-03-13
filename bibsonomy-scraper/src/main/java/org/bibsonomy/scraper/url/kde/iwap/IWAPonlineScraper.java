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
package org.bibsonomy.scraper.url.kde.iwap;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;
import org.bibsonomy.util.WebUtils;

/**
 * Scraper for papers from http://www.iwaponline.com
 * @author tst
 */
public class IWAPonlineScraper extends GenericBibTeXURLScraper {
	private static final Log log = LogFactory.getLog(IWAPonlineScraper.class);
	
	private static final String SITE_NAME = "IWA Publishing";

	private static final String SITE_URL = "http://www.iwaponline.com";

	private static final String INFO = "This Scraper supports papers from " + href(SITE_URL, SITE_NAME) +".";

	/*
	 * host
	 */

	private static final String HOST = "iwaponline.com";

	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + HOST), AbstractUrlScraper.EMPTY_PATTERN));

	private static final Pattern BIBTEX_PATTERN = Pattern.compile("<a.*href=\"([^\"]+)\".*>BibTeX</a>");
	private static final Pattern PATTERN_ABSTRACT = Pattern.compile("(?i)ABSTRACT.*\\s+<P>(.*)\\s+</P>");

	@Override
	public String getInfo() {
		return INFO;
	}

	private static String abstractPrser(URL url){
		try {
			final Matcher m = PATTERN_ABSTRACT.matcher(WebUtils.getContentAsString(url));
			if(m.find()) {
				return m.group(1);
			}
		} catch(Exception e) {
			log.error("error while getting abstract for " + url, e);
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

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.generic.AbstractGenericFormatURLScraper#getDownloadURL(java.net.URL, java.lang.String)
	 */
	@Override
	protected String getDownloadURL(URL url, String cookies) throws ScrapingException, IOException {
		try {
			final String content = WebUtils.getContentAsString(url, cookies);
			final Matcher m = BIBTEX_PATTERN.matcher(content);
			if (m.find()) {
				return "http://" + url.getHost() + m.group(1);
			}
		} catch (final IOException e) {
			throw new ScrapingException(e);
		}
		return null;
	}
}
