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
package org.bibsonomy.scraper.url.kde.openrepository;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.generic.GenericRISURLScraper;

/**
 * Scraper for openrepository pages
 * @author tst
 */
public class OpenrepositoryScraper extends GenericRISURLScraper {

	private static final String SITE_URL = "http://openrepository.com/";
	private static final String SITE_NAME = "Open Repository";
	private static final String SUPPORTED_HOST_OPENREPOSITORY = "openrepository.com";
	private static final String SUPPORTED_HOST_E_SPACE = "e-space.mmu.ac.uk";
	private static final String SUPPORTED_HOST_E_SPACE_PATH = "/e-space";
	private static final String SUPPORTED_HOST_HIRSLA = "hirsla.lsh.is";
	private static final String SUPPORTED_HOST_HIRSLA_PATH = "/lsh";
	private static final String SUPPORTED_HOST_GTCNI = "arrts.gtcni.org.uk";
	private static final String SUPPORTED_HOST_GTCNI_PATH = "/gtcni";
	private static final String SUPPORTED_HOST_EXETER = "eric.exeter.ac.uk";
	private static final String SUPPORTED_HOST_EXETER_PATH = "/exeter";
	private static final String PATTERN_HANDLE = "handle/(.*)";
	private static final String INFO = "Supports the following repository: " + href(SITE_URL, SITE_NAME) + ".";
	private static final List<Pair<Pattern,Pattern>> patterns = new LinkedList<Pair<Pattern,Pattern>>(); 
	
	static {
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + SUPPORTED_HOST_OPENREPOSITORY), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + SUPPORTED_HOST_E_SPACE), Pattern.compile(SUPPORTED_HOST_E_SPACE_PATH + ".*")));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + SUPPORTED_HOST_EXETER), Pattern.compile(SUPPORTED_HOST_EXETER_PATH + ".*")));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + SUPPORTED_HOST_GTCNI), Pattern.compile(SUPPORTED_HOST_GTCNI_PATH + ".*")));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + SUPPORTED_HOST_HIRSLA), Pattern.compile(SUPPORTED_HOST_HIRSLA_PATH + ".*")));
	}
	
	@Override
	public String getInfo() {
		return INFO;
	}
	
	/**
	 * get handle id from url
	 * @param url
	 * @return id, null if matching failed
	 */
	private static String getHandle(String url) {
		Pattern handlePattern = Pattern.compile(PATTERN_HANDLE);
		Matcher handleMatcher = handlePattern.matcher(url);
		if (handleMatcher.find()) {
			return handleMatcher.group(1);
		}
		return null;
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
	public String getDownloadURL(URL url) {
		final String sturl = url.toString();
		if (sturl.contains(SUPPORTED_HOST_OPENREPOSITORY)) {
			return "http://www." + SUPPORTED_HOST_OPENREPOSITORY + "/references?format=refman&handle=" + getHandle(sturl);
		} else if(sturl.contains(SUPPORTED_HOST_E_SPACE + SUPPORTED_HOST_E_SPACE_PATH)) {
			return  "http://www." + SUPPORTED_HOST_E_SPACE + SUPPORTED_HOST_E_SPACE_PATH + "/references?format=refman&handle=" + getHandle(sturl);
		}else if(sturl.contains(SUPPORTED_HOST_EXETER + SUPPORTED_HOST_EXETER_PATH)) {
			return "http://www." + SUPPORTED_HOST_EXETER + SUPPORTED_HOST_EXETER_PATH + "/references?format=refman&handle=" + getHandle(sturl);
		}else if(sturl.contains(SUPPORTED_HOST_HIRSLA + SUPPORTED_HOST_HIRSLA_PATH)){
			return "http://www." + SUPPORTED_HOST_HIRSLA + SUPPORTED_HOST_HIRSLA_PATH + "/references?format=refman&handle=" + getHandle(sturl);
		}else if(sturl.contains(SUPPORTED_HOST_GTCNI + SUPPORTED_HOST_GTCNI_PATH)){
			return "http://" + SUPPORTED_HOST_GTCNI + SUPPORTED_HOST_GTCNI_PATH + "/references?format=refman&handle=" + getHandle(sturl);
		}
		return null;
	}
}
