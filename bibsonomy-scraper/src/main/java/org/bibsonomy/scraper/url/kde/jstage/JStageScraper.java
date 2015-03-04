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
package org.bibsonomy.scraper.url.kde.jstage;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ReferencesScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;
import org.bibsonomy.util.WebUtils;

/**
 * @author Haile
 */
public class JStageScraper extends GenericBibTeXURLScraper implements ReferencesScraper {
	private static final Log log = LogFactory.getLog(JStageScraper.class);
	
	private static final String SITE_NAME = "J-Stage";
	private static final String SITE_URL = "https://jstage.jst.go.jp";
	private static final String INFO = "Extracts publications from " + href(SITE_URL, SITE_NAME) + 
			". Publications can be entered as a selected BibTeX snippet or by posting the page of the reference.";
	private static final List<Pair<Pattern, Pattern>> URL_PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "jstage.jst.go.jp"), AbstractUrlScraper.EMPTY_PATTERN));
	private static final Pattern PATTERN_ABSTRACT = Pattern.compile("<p class=\"normal\"\\s*>\\s+<br>\\s+(.*)\\s+</p>");
	private static final Pattern PATTERN_REFERENCES = Pattern.compile("(?s)<ul class=\"mod-list-citation\">(.*)</ul>");
	
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
	private static String abstractParser(URL url){
		try{
			Matcher m = PATTERN_ABSTRACT.matcher(WebUtils.getContentAsString(url));
			if(m.find()) {
				return m.group(1);
			}
		} catch (final IOException e) {
			log.error("error while getting abstract " + url, e);
		}
		return null;
	}
	
	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return URL_PATTERNS;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.generic.PostprocessingGenericURLScraper#postProcessScrapingResult(org.bibsonomy.scraper.ScrapingContext, java.lang.String)
	 */
	@Override
	protected String postProcessScrapingResult(ScrapingContext sc, String result) {
		result = result.replace(result.split(",")[0],result.split(",")[0].replace(" ", ""));
		result = BibTexUtils.addFieldIfNotContained(result, "url", sc.getUrl().toString());
		result = BibTexUtils.addFieldIfNotContained(result, "abstract", abstractParser(sc.getUrl()));
		return StringEscapeUtils.unescapeHtml(result);
	}
	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.generic.SimpleGenericURLScraper#getBibTeXURL(java.net.URL)
	 */
	@Override
	public String getDownloadURL(URL url) throws ScrapingException {
		final String[] bibPath = url.getPath().split("/");
		try {
			return "https://" + url.getHost() + "/AF06S010ShoshJkuDld?sryCd=" + bibPath[2] + "&noVol=" + bibPath[3] + "&noIssue=" + bibPath[4] + "&kijiCd=" + bibPath[5] + "&kijiLangKrke=en&kijiToolIdHkwtsh=AT0073";
		} catch (Exception e) {
			log.error("error while getting bibtex url for " + url, e);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.ReferencesScraper#scrapeReferences(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean scrapeReferences(ScrapingContext sc) throws ScrapingException {
		try {
			final Matcher m = PATTERN_REFERENCES.matcher(WebUtils.getContentAsString(sc.getUrl().toString() + "/references"));
			if(m.find()) {
				sc.setReferences(m.group(1));
				return true;
			}
		} catch (IOException e) {
			log.error("error while getting references " + sc.getUrl(), e);
		}
		return false;
	}
}
