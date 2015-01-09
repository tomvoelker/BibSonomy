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
package org.bibsonomy.scraper.url.kde.ams;

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
 * Scraper for ams.allenpress.com
 * @author tst
 */
public class AmsScraper extends GenericBibTeXURLScraper {
	private static final Log log = LogFactory.getLog(AmsScraper.class);
	
	private static final String SITE_NAME = "American Meteorological Society";
	private static final String SITE_URL = "http://journals.ametsoc.org";
	private static final String INFO = "For references from the "+href(SITE_URL, SITE_NAME)+".";
	
	private static final String FORMAT_BIBTEX = "&format=bibtex";


	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*journals.ametsoc.org"), AbstractUrlScraper.EMPTY_PATTERN));
	
	private static final Pattern pattern = Pattern.compile("doi/\\w+/([^&]*)[&]?");
	private static final Pattern abstractPattern = Pattern.compile("Abstract.*<p class=\"last\">(.*)</p>");
	
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
	
	private static String abstactParser(final URL url){
		try {
			final Matcher m = abstractPattern.matcher(WebUtils.getContentAsString("http://journals.ametsoc.org/doi/abs/" + doiExtracter(url)));
			if (m.find()) {
				return m.group(1);
			}
		} catch(Exception e){
			log.error("error while getting abstract for " + url, e);
		}
		return null;
	}
	
	private static String doiExtracter(URL url){
		final Matcher matcher = pattern.matcher(url.toString());
		if (matcher.find()) 
			return matcher.group(1).replace("%2F", "/");
		return null;
	}
	
	@Override
	public String getDownloadURL(URL url) throws ScrapingException {
		return "http://journals.ametsoc.org/action/downloadCitation?doi=" + doiExtracter(url) + "&include=cit" + FORMAT_BIBTEX;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.generic.PostprocessingGenericURLScraper#postProcessScrapingResult(org.bibsonomy.scraper.ScrapingContext, java.lang.String)
	 */
	@Override
	protected String postProcessScrapingResult(ScrapingContext sc, String result) {
		return BibTexUtils.addFieldIfNotContained(result, "abstract", abstactParser(sc.getUrl()));
	}
}
