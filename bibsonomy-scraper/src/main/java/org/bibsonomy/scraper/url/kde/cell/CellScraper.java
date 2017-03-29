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
package org.bibsonomy.scraper.url.kde.cell;

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
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericRISURLScraper;
import org.bibsonomy.util.WebUtils;

/**
 * @author tst
 */
public class CellScraper extends GenericRISURLScraper{
	private static final Log log = LogFactory.getLog(CellScraper.class);
	
	private static final String SITE_NAME = "Cell";
	private static final String SITE_URL = "http://www.cell.com/";
	private static final String INFO = "Scraper for Journals from " + href(SITE_URL, SITE_NAME)+".";
	private static final Pattern patternId = Pattern.compile("pii:(.*?);");
	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "cell.com"), AbstractUrlScraper.EMPTY_PATTERN));
	
	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}


	@Override
	public String getInfo() {
		return INFO;
	}

	@Override
	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	@Override
	public String getSupportedSiteURL() {
		return SITE_URL;
	}
	private static String extractId(URL url){
		try {
			final Matcher m = patternId.matcher(WebUtils.getContentAsString(url.toString()));
			if(m.find()) {
				return m.group(1);
			}
		} catch (IOException e) {
			log.error("article id parsing failed " + url, e);
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.generic.AbstractGenericFormatURLScraper#getDownloadURL(java.net.URL)
	 */
	@Override
	protected String getDownloadURL(URL url, String cookies) throws ScrapingException {
		final String contentID = extractId(url);
		final String downloadUrl = "http://" + url.getHost().toString() + "/action/downloadCitation?objectUri=pii:" + contentID + "&direct=true&include=abs&submit=Export";
		return downloadUrl;
	}

}
