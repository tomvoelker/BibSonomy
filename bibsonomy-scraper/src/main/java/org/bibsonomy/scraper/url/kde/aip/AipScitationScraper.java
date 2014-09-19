/**
 *
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.scraper.url.kde.aip;

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
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;
import org.bibsonomy.util.WebUtils;


/**
 * Scraper for scitation.aip.org
 * It supports following urls:
 * - http://scitation.aip.org/vsearch/servlet/VerityServlet?
 * - http://scitation.aip.org/getabs/servlet/GetCitation?
 * - http://jcp.aip.orgs
 * @author tst
 *
 */
public class AipScitationScraper extends GenericBibTeXURLScraper {
	private static final Log log = LogFactory.getLog(AipScitationScraper.class);
	
	private static final String SITE_NAME = "AIP Scitation";
	private static final String SITE_URL = "http://scitation.aip.org/";
	private static final String INFO = "Extracts publications from " + href(SITE_URL, SITE_NAME) + ". Publications can be entered as a selected BibTeX snippet or by posting the page of the reference.";
	private static final Pattern hostPattern = Pattern.compile(".*" + "aip.org");
	private static final Pattern pathPattern = AbstractUrlScraper.EMPTY_PATTERN;
	private static final String BIBTEX_PATH = "/cite/bibtex";
	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(hostPattern, pathPattern));
	private static final Pattern abstractPattern = Pattern.compile("<meta name=\"citation_abstract\" content=\"(.*)\"\\s*/>");
	private static final Pattern firstPagePattern = Pattern.compile("<meta name=\"citation_firstpage\" content=\"(.*)\" />");
	
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
	
	private static String abstractParser(URL url){
		try{
			Matcher m = abstractPattern.matcher(WebUtils.getContentAsString(url));
			if (m.find()) {
				return m.group(1);
			}
		} catch (final Exception e) {
			log.error("error while getting abstract for " + url, e);
		}
		return null;
	}
	
	private static String firstPageParser(URL url){
		try{
			Matcher m = firstPagePattern.matcher(WebUtils.getContentAsString(url));
			if (m.find()) {
				return m.group(1);
			}
		} catch (final Exception e) {
			log.error("error while getting abstract for " + url, e);
		}
		return null;
	}
	
	@Override
	public String getDownloadURL(URL url) throws ScrapingException {
		return "http://" + url.getHost().toString() + url.getPath().toString() + BIBTEX_PATH;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.generic.PostprocessingGenericURLScraper#postProcessScrapingResult(org.bibsonomy.scraper.ScrapingContext, java.lang.String)
	 */
	@Override
	protected String postProcessScrapingResult(ScrapingContext sc, String result) {
		// add an abstract
		String bibtex = BibTexUtils.addFieldIfNotContained(result, "abstract", abstractParser(sc.getUrl()));
		// fix the eid
		bibtex = bibtex.replace("eid = ,", "eid = " + firstPageParser(sc.getUrl()) + ",");
		return bibtex;
	}
}
