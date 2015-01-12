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
package org.bibsonomy.scraper.url.kde.ats;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.CitedbyScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericRISURLScraper;
import org.bibsonomy.util.WebUtils;

/**
 * @author clemens
 */
public class ATSScraper extends GenericRISURLScraper implements CitedbyScraper{
	private static final Log log = LogFactory.getLog(ATSScraper.class);
	
	private static final String SITE_NAME = "American Thoracic Society Journals";
	private static final String SITE_URL = "http://www.atsjournals.org/";
	private static final String INFO = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);
	private static final List<Pair<Pattern, Pattern>> URL_PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "atsjournals.org"),AbstractUrlScraper.EMPTY_PATTERN));
	private static final String BIBTEX_URL = "http://www.atsjournals.org/action/downloadCitation?doi=";
	private static final Pattern ID_PATTERN = Pattern.compile("\\d+.*");
	private static final int ID_GROUP = 0;
	
	private static final Pattern ABSTRACT_PATTERN = Pattern.compile("<div class=\"abstractSection\">(.*?)</div>");	
	private static final Pattern CITEDBY = Pattern.compile("<div class=\"citedByEntry\">(.*)</div></div></div>");
	private static String extractId(final String url) {
		final Matcher matcher = ID_PATTERN.matcher(url);
		if (matcher.find()) {
			return matcher.group(ID_GROUP);
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
		return URL_PATTERNS;
	}

	private static String abstractParser(URL url){
		try {
			final String cookie = WebUtils.getCookies(url);
			final Matcher m = ABSTRACT_PATTERN.matcher(WebUtils.getContentAsString(url.toString(),cookie));
			if (m.find()) {
				return m.group(1);
			}
		} catch (Exception e) {
			log.error("error while getting abstract " + url, e);
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.generic.RISGenericURLScraper#postProcessScrapingResult(org.bibsonomy.scraper.ScrapingContext, java.lang.String)
	 */
	@Override
	protected String postProcessScrapingResult(ScrapingContext scrapingContext, String bibtex) {
		return BibTexUtils.addFieldIfNotContained(bibtex,"abstract",abstractParser(scrapingContext.getUrl()));
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.generic.RISGenericURLScraper#getRISURL(java.net.URL)
	 */
	@Override
	protected String getDownloadURL(URL url) {
		final String id = extractId(url.toString());
		return BIBTEX_URL + id;
	}
	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.CitedbyScraper#scrapeCitedby(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean scrapeCitedby(ScrapingContext sc) throws ScrapingException {
		try{
			final Matcher m = CITEDBY.matcher(WebUtils.getContentAsString(sc.getUrl().toString()));
			if(m.find()) {
				sc.setCitedBy(m.group(1));
				return true;
			}			
		} catch (Exception e) {
			log.error("error while getting cited by " + sc.getUrl().toString(), e);
		}
		return false;
	}
}

