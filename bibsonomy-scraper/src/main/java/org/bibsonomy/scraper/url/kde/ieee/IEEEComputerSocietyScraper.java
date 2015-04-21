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
package org.bibsonomy.scraper.url.kde.ieee;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
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
 * Scraper for csdl2.computer.org
 * @author tst
 */ 
public class IEEEComputerSocietyScraper extends GenericBibTeXURLScraper {
	private static final Log log = LogFactory.getLog(IEEEComputerSocietyScraper.class);
	private static final String SITE_NAME = "IEEE Computer Society";
	private static final String SITE_URL = "http://www.computer.org/portal/web/guest/home";
	private static final String INFO = "Scraper for publications from " + href(SITE_URL, SITE_NAME);
	private static final String HOST_OLD= "csdl2.computer.org";
	private static final String HOST_NEW = "computer.org";
	
	private static final Pattern abstractPattern = Pattern.compile("<meta property=\"og:description\" content=\"(.*?)\" />");
	
	private static final List<Pair<Pattern, Pattern>> patterns = new LinkedList<Pair<Pattern,Pattern>>();
	
	static{
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + HOST_OLD), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + HOST_NEW), AbstractUrlScraper.EMPTY_PATTERN));
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
	public String getInfo() {
		return INFO;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.generic.AbstractGenericFormatURLScraper#getDownloadURL(java.net.URL)
	 */
	@Override
	protected String getDownloadURL(URL url) throws ScrapingException {
		if (url.toString().endsWith(".pdf")) {
			return  url.toString().replaceAll(".pdf", "-reference.bib");
		}
		return  url.toString().replaceAll("-.*", "-reference.bib");
	}
	
	@Override
	protected String postProcessScrapingResult(ScrapingContext scrapingContext, String bibtex) {
		try {
			final Matcher m = abstractPattern.matcher(WebUtils.getContentAsString(scrapingContext.getUrl().toString()));
			if (m.find())
				return BibTexUtils.addFieldIfNotContained(bibtex, "abstract", m.group(1));
		} catch(IOException e) {
			log.debug("Abstract is not found" + e.getMessage());
		}
		return bibtex;
	}
}
