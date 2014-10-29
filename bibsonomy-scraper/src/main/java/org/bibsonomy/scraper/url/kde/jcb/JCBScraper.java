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

package org.bibsonomy.scraper.url.kde.jcb;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;

/**
 * @author hagen
 */
public class JCBScraper extends GenericBibTeXURLScraper {

	private static final String SITE_NAME = "JCB";
	private static final String SITE_URL = "http://jcb.rupress.org/";
	private static final String INFO = "This Scraper parses a publication from " + href(SITE_URL, SITE_NAME)+".";
	
	private static final List<Pair<Pattern,Pattern>> PATTERNS = new ArrayList<Pair<Pattern,Pattern>>();
	
	static {
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile("jcb.rupress.org"), Pattern.compile("content")));
	}
	
	private static final Pattern ID_PATTERN = Pattern.compile("(/\\d++){3}");

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
	public String getDownloadURL(URL url) throws ScrapingException {
		Matcher m = ID_PATTERN.matcher(url.toExternalForm());
		if (!m.find()) return null;
		String result = null;
		try {
			result = URLEncoder.encode(m.group().replaceFirst("/", ";"), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			return null;
		}
		return "http://jcb.rupress.org/citmgr?type=bibtex&gca=jcb" + result;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
	}

}
