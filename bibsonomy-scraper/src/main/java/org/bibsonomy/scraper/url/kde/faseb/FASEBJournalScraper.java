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
package org.bibsonomy.scraper.url.kde.faseb;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;

/**
 * @author wla
 */
public class FASEBJournalScraper extends GenericBibTeXURLScraper {

	private final Log log = LogFactory.getLog(FASEBJournalScraper.class);

	private static final String SITE_NAME = "The FASEB Journal";
	private static final String SITE_URL = "http://www.fasebj.org";
	private static final String INFO = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);

	private static final List<Pair<Pattern, Pattern>> PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "www.fasebj.org"), AbstractUrlScraper.EMPTY_PATTERN));

	/*
	 * This regex is for extraction of 
	 *  -"15/14/2565" from
	 *   http://www.fasebj.org/content/15/14/2565.abstract
	 *   
	 *  -"26/8/3100" from 
	 *   http://www.fasebj.org/content/26/8/3100.full
	 *   http://www.fasebj.org/content/26/8/3100.short
	 *   
	 *  -"fj.12-211441" from
	 *   http://www.fasebj.org/content/early/2012/06/15/fj.12-211441.short
	 *  
	 */
	private static final Pattern URL_ID_PATTERN = Pattern.compile("(?:(\\d*?/\\d*?/\\d*?)\\.(?:(?:abstract)|(?:full)|(?:short)))|(fj\\.\\d*-\\d*)");

	private static final String BIBTEX_URL = "http://www.fasebj.org/citmgr?type=bibtex&gca=fasebj;";
	/**
	 * extracts publication id form url
	 * 
	 * @param url
	 *            to extract
	 * @return document id or <code>null</code> if no id parsed
	 */
	private String extractId(final String url) {
		final Matcher matcher = URL_ID_PATTERN.matcher(url);
		if (matcher.find()) {
			for (int i = 1; i <= matcher.groupCount(); i++) {
				final String id = matcher.group(i);
				if (present(id)) {
					return id;
				}
			}
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
		return INFO;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
	}

	@Override
	public String getDownloadURL(URL url, String cookies) throws ScrapingException {
		final String id = extractId(url.toString());
		if (present(id)) {
			return BIBTEX_URL + id;
		}
		return null;
	}
}
