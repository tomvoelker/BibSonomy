/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scraper for openrepository pages
 * @author tst
 */
public class OpenrepositoryScraper extends GenericBibTeXURLScraper {

	private static final String SITE_URL = "http://openrepository.com/";
	private static final String SITE_NAME = "Open Repository";
	private static final String SUPPORTED_HOST_OPENREPOSITORY = "openrepository.com";
	private static final String PATTERN_HANDLE = "handle/(.*)";
	private static final String INFO = "Supports the following repository: " + href(SITE_URL, SITE_NAME) + ".";
	private static final List<Pair<Pattern,Pattern>> patterns = new LinkedList<Pair<Pattern,Pattern>>(); 
	
	static {
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + SUPPORTED_HOST_OPENREPOSITORY), Pattern.compile("handle")));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "repository\\..*\\.[A-z]+"), Pattern.compile("handle")));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "stor.scot.nhs.uk"), Pattern.compile("handle")));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "fieldresearch.msf.org"), Pattern.compile("handle")));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "t-stor.teagasc.ie"), Pattern.compile("handle")));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "soar.usi.edu"), Pattern.compile("handle")));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "oar.marine.ie"), Pattern.compile("handle")));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "scholarworks.alaska.edu"), Pattern.compile("handle")));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "ir.icscanada.edu"), Pattern.compile("handle")));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "lenus.ie"), Pattern.compile("handle")));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "repositorioacademico.upc.edu.pe"), Pattern.compile("handle")));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "hirsla.lsh.is"), Pattern.compile("handle")));
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
	public String getDownloadURL(URL url, String cookies) {
		return "https://" + url.getHost() + "/discover/export?format=bibtex&handle=" + getHandle(url.toString());
	}
}
