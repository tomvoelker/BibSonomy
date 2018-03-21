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
package org.bibsonomy.scraper.url.kde.wileyintersience;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.generic.LiteratumScraper;


/**
 * Scraper for www3.interscience.wiley.com
 * @author rja
 */
public class WileyIntersienceScraper extends LiteratumScraper {

	private static final String SITE_HOST = "onlinelibrary.wiley.com";
	private static final String SITE_URL  = "http://" + SITE_HOST + "/";
	private static final String SITE_NAME = "Wiley Online Library";
	private static final String SITE_INFO = "Extracts publications from the abstract page of " + href(SITE_URL,SITE_NAME) + ".";
	private static final Pattern DOI_PATTERN = Pattern.compile("/doi/.+");

	private static final List<Pair<Pattern,Pattern>> PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + SITE_HOST), DOI_PATTERN)); 

	@Override
	protected boolean requiresCookie() {
		return true;
	}
	@Override
	protected List<NameValuePair> getPostContent(String doi) {
		final ArrayList<NameValuePair> postData = new ArrayList<NameValuePair>(6);
		postData.add(new BasicNameValuePair("doi", doi));
		postData.add(new BasicNameValuePair("include", "abs"));
		postData.add(new BasicNameValuePair("downloadFileName", "foo"));
		postData.add(new BasicNameValuePair("format", "bibtex"));
		postData.add(new BasicNameValuePair("direct", "direct"));
		postData.add(new BasicNameValuePair("submit", "Download"));
		return postData;
	}

	@Override
	public String getInfo() {
		return SITE_INFO;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
	}

	@Override
	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	@Override
	public String getSupportedSiteURL() {
		return SITE_URL;
	}
}
