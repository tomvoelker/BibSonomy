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
package org.bibsonomy.scraper.url.kde.bmj;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ReferencesScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;
import org.bibsonomy.util.WebUtils;

/**
 * @author wbi
 */
public class BMJScraper extends GenericBibTeXURLScraper implements ReferencesScraper{
	private static final String SITE_NAME = "BMJ";
	private static final String SITE_URL = "http://www.bmj.com/";
	private static final String INFO = "This Scraper parses a publication from " + href(SITE_URL, SITE_NAME)+".";

	private static final Pattern BIBTEX_PATTERN = Pattern.compile("<li .*>BibTeX .*<a href=\"(.*)\" .*>Download</a></li>");
	private static final Pattern REFERENCES_PATTERN = Pattern.compile("<ol class=\"cit-list\">(.*)</ol>");
	private static final List<Pair<Pattern, Pattern>> URL_PATTERNS = new ArrayList<Pair<Pattern,Pattern>>();
	
	static {
		URL_PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*?" + "www.bmj.com"), AbstractUrlScraper.EMPTY_PATTERN));
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
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.generic.AbstractGenericFormatURLScraper#getDownloadURL(java.net.URL)
	 */
	@Override
	protected String getDownloadURL(URL url, String cookies) throws ScrapingException {
		String st_url = SITE_URL;
		try{
			Matcher m = BIBTEX_PATTERN.matcher(WebUtils.getContentAsString(url));
			if (m.find()) {
				st_url += m.group(1);
				return st_url;
			}
		} catch (IOException e) {
			throw new ScrapingException(e);
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.ReferencesScraper#scrapeReferences(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean scrapeReferences(ScrapingContext scrapingContext) throws ScrapingException {
		String references = null;
		try {
			final Matcher m = REFERENCES_PATTERN.matcher(WebUtils.getContentAsString(scrapingContext.getUrl()));
			if (m.find()) {
				references = m.group(1);
			}
			if (references != null) {
				scrapingContext.setReferences(references);
				return true;
			}
		} catch (IOException e) {
			throw new ScrapingException(e);
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.generic.AbstractGenericFormatURLScraper#postProcessScrapingResult(org.bibsonomy.scraper.ScrapingContext, java.lang.String)
	 */
	@Override
	protected String postProcessScrapingResult(ScrapingContext scrapingContext, String bibtex) {
		// add bibtex key if not present
		final String[] allfields = bibtex.split("\n");
		final String[] firstline = allfields[0].split("\\{");
		
		if (firstline.length == 1) {
			// TODO: shouldn't we only replace the first match?
			// TODO: generate a nice key
			final String bibtex_replace = allfields[0].replace("{","{noKey,");
			return bibtex.replace(allfields[0], bibtex_replace);
		}
		return bibtex;
	}
}
