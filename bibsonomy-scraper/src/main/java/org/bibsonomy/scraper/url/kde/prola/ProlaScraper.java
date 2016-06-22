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
package org.bibsonomy.scraper.url.kde.prola;

import java.io.IOException;
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
import org.bibsonomy.scraper.ReferencesScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;
import org.bibsonomy.util.WebUtils;


/**
 * Scraper for prola.aps.org. It scrapes selected bibtex snippets and selected articles.
 * @author tst
 */
public class ProlaScraper extends GenericBibTeXURLScraper implements ReferencesScraper, CitedbyScraper {
	private static final Log log = LogFactory.getLog(ProlaScraper.class);
	private static final String SITE_NAME = "PROLA";
	private static final String PROLA_APS_URL_BASE = "http://prola.aps.org";
	private static final String SITE_URL = PROLA_APS_URL_BASE+"/";
	private static final String INFO = "For selected BibTeX snippets and articles from " + href(SITE_URL , SITE_NAME)+".";

	/*
	 * needed URLs and components
	 */
	private static final String PROLA_APS_HOST = ".aps.org";

	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + PROLA_APS_HOST), AbstractUrlScraper.EMPTY_PATTERN));
	private static final Pattern PATTERN_ABSTRACT = Pattern.compile("<meta name=\"description\" content=\"(.*)\">");
	private static final Pattern PATTERN_URL = Pattern.compile("<meta content=\"(http.*?)\" property=\"og:url\" />");
	private static final Pattern PATTERN_CITEDBY = Pattern.compile("(?s)<div class=\"large-9 columns\">(.*)</div><div class=\"pagination-centered\">");
	
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

	@Override
	public String getDownloadURL(URL url, String cookies) throws ScrapingException {
		return url.toString().replace("abstract", "export");
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.generic.PostprocessingGenericURLScraper#postProcessScrapingResult(org.bibsonomy.scraper.ScrapingContext, java.lang.String)
	 */
	@Override
	protected String postProcessScrapingResult(ScrapingContext sc, String result) {
		return BibTexUtils.addFieldIfNotContained(result, "abstract", abstractParser(sc.getUrl()));
	}
	
	private static String abstractParser(URL url){
		try{
			Matcher m = PATTERN_ABSTRACT.matcher(WebUtils.getContentAsString(url));
			if(m.find()) {
				return m.group(1);
			}
		} catch (Exception e) {
			log.error("error while getting abstract for " + url, e);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.CitedbyScraper#scrapeCitedby(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean scrapeCitedby(ScrapingContext sc) throws ScrapingException {
		try {
			final Matcher m = PATTERN_URL.matcher(WebUtils.getContentAsString(sc.getUrl()));
			if(m.find()) {
				final Matcher m2 = PATTERN_CITEDBY.matcher(WebUtils.getContentAsString(m.group(1).replaceAll("pdf|abstract|article|export", "cited-by")));
				if(m2.find()) {
					sc.setCitedBy(m2.group(1));
					return true;
				}
			}
		} catch (IOException e) {
			log.error("error while getting cited by articles for " + sc.getUrl(), e);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.ReferencesScraper#scrapeReferences(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean scrapeReferences(ScrapingContext sc) throws ScrapingException {
		try {
			final Matcher m = PATTERN_URL.matcher(WebUtils.getContentAsString(sc.getUrl()));
			if(m.find()) {
				sc.setReferences(WebUtils.getContentAsString(m.group(1).replaceAll("pdf|abstract|export", "article") + "/section/references"));
				return true;
			}
		} catch (IOException e) {
			log.error("error while getting references for " + sc.getUrl(), e);
		}
		return false;
	}
}
