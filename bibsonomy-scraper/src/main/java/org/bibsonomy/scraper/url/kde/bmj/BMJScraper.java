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

package org.bibsonomy.scraper.url.kde.bmj;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.scraper.generic.CitationManagerScraper;
import org.bibsonomy.util.WebUtils;

/**
 * @author wbi
 */
public class BMJScraper extends CitationManagerScraper {

	private static final String SITE_NAME = "BMJ";
	private static final String SITE_URL = "http://www.bmj.com/";
	private static final String INFO = "This Scraper parses a publication from " + href(SITE_URL, SITE_NAME)+".";

	private static final Pattern DOWNLOAD_LINK_PATTERN = Pattern.compile("<a href=\"([^\"]++)\"[^>]*+>Download to citation manager</a>");
	private static final Pattern CITATION_MANAGER_PATTERN = Pattern.compile("href=\"(/highwire/citation/\\d++/bibtex)\"");
	
	private static final List<Pair<Pattern, Pattern>> URL_PATTERNS = new ArrayList<Pair<Pattern,Pattern>>();
	
	static {
		URL_PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*?" + "www.bmj.com"), AbstractUrlScraper.EMPTY_PATTERN));
	}

	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	public String getSupportedSiteURL() {
		return SITE_URL;
	}

	public String getInfo() {
		return INFO;
	}

	@Override
	public Pattern getDownloadLinkPattern() {
		return DOWNLOAD_LINK_PATTERN;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return URL_PATTERNS;
	}
	
	@Override
	protected String buildDownloadLink(URL url, String content) throws ScrapingFailureException {

		// get link to "download to citation manager" page
		final Matcher downloadLinkMatcher = getDownloadLinkPattern().matcher(content);
		
		//throw exception if download link "download to citation manager" not found
		if(!downloadLinkMatcher.find())
			throw new ScrapingFailureException("Download link is not available");
		
		try {
			//get download link
			String downloadPage = WebUtils.getContentAsString("http://" + url.getHost() + downloadLinkMatcher.group(1));
			Matcher m2 = CITATION_MANAGER_PATTERN.matcher(downloadPage);
			if (!m2.find())
				throw new ScrapingFailureException("Download link is not available");
			return "http://" + url.getHost() + m2.group(1);
		} catch (IOException ex) {
			throw new ScrapingFailureException(ex);
		}
	}

}
