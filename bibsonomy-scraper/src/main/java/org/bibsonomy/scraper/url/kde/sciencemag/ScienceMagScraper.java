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

package org.bibsonomy.scraper.url.kde.sciencemag;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.net.URL;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.generic.CitationManagerScraper;

/**
 * @author clemens
 */
public class ScienceMagScraper extends CitationManagerScraper {
	// <li><a href="/citmgr?gca=sci;276/5317/1425">Download Citation</a></li>
	private static final Pattern DOWNLOAD_LINK_PATTERN = Pattern.compile("<a href=\"(.+?)\">Download Citation</a>");
	private static final String SITE_NAME = "Science Magazine";
	private static final String SITE_URL = "http://www.sciencemag.org/";
	private static final String INFO = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);
	private static final List<Pair<Pattern, Pattern>> URL_PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(
			Pattern.compile(".*" + "sciencemag.org"), 
			Pattern.compile("/content" + ".*")
		));

        /** 
	 * If the IP where the scraper is run has not access to the full text, URLs ending with 
	 * ".full" (e.g., http://www.sciencemag.org/content/302/5651/1704.full) do not contain
	 * the BibTeX download link. If we modify the URL to ".short" (e.g., 
	 * http://www.sciencemag.org/content/302/5651/1704.short), the link is contained.
	 */
	protected boolean scrapeInternal(final ScrapingContext sc) throws ScrapingException {
	    final URL url = sc.getUrl();
	    if (url != null) {
		final String path = url.getPath();
		if (path.endswith(".long")) {
		    path.substr(
		}
	    }
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
}
