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
package org.bibsonomy.scraper.url.kde.jap;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ReferencesScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericRISURLScraper;
import org.bibsonomy.util.WebUtils;

/**
 * @author hagen
 */
public class JAPScraper extends GenericRISURLScraper implements ReferencesScraper{
	private static final Log log = LogFactory.getLog(JAPScraper.class);
	private static final String SITE_NAME = "Journal of Applied Physiology";
	private static final String SITE_URL = "http://jap.physiology.org/";
	private static final String INFO = "This Scraper parses a publication from " + href(SITE_URL, SITE_NAME)+".";

	private static final Pattern RIS_URL = Pattern.compile("<li class=\"ris\"><a href=\"(.+?)\".*?>RIS</a></li>");
	
	private static final List<Pair<Pattern, Pattern>> URL_PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "jap.physiology.org"), AbstractUrlScraper.EMPTY_PATTERN));
	private static final Pattern REFERENCES_PATTERN = Pattern.compile("(?s)<h2>REFERENCES</h2>(.*)<span class=\"highwire-journal-article-marker-end\"></span>");

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
		try {
			final Matcher m = RIS_URL.matcher(WebUtils.getContentAsString(url.toString()));
			if (m.find()) {
				return "http://" + url.getHost().toString() + m.group(1);
			}
		} catch (IOException e) {
			log.error("Download link not found", e);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.ReferencesScraper#scrapeReferences(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean scrapeReferences(ScrapingContext sc) throws ScrapingException {
		try {
			final Matcher m = REFERENCES_PATTERN.matcher(WebUtils.getContentAsString(sc.getUrl().toString()));
			if (m.find()) {
				sc.setReferences(m.group(1));
				return true;
			}
		} catch (IOException e) {
			log.error("Download link not found", e);
		}
		return false;
	}

}
