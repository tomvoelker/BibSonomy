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
package org.bibsonomy.scraper.url.kde.cshlp;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;
import org.bibsonomy.util.WebUtils;

/**
 * This scraper supports download links from the following hosts
 * 1. cshperspectives.cshlp.org
 * 2. jbc.org
 * 3. cancerres.aacrjournals.org
 * 4. jimmunol.org
 * 
 * @author Mohammed Abed
 */
public class CSHLPScraper extends GenericBibTeXURLScraper {
	private static final String SITE_NAME = "Cold Spting Harbor Perspetives in Biology";
	private static final String SITE_URL = "http://cshperspectives.cshlp.org/";
	private static final String info = "This scraper parses a publication page of citations from " + href(SITE_URL, SITE_NAME) + ".";
	private static final String CSHLP_HOST = "cshperspectives.cshlp.org";
	private static final String JBC_HOST = "jbc.org";
	private static final String CANCERRES_AACJOURNALS_HOST = "cancerres.aacrjournals.org";
	private static final String JIMMUNOL_HOST = "jimmunol.org";
	private static final String HTTP = "http://";
	
	private static final String CONTENT_SUBPATH = "/content/";
	private static final List<Pair<Pattern, Pattern>> patterns = new LinkedList<Pair<Pattern, Pattern>>();
	static {
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + CSHLP_HOST), Pattern.compile(CONTENT_SUBPATH)));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + JBC_HOST), Pattern.compile(CONTENT_SUBPATH)));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + CANCERRES_AACJOURNALS_HOST), Pattern.compile(CONTENT_SUBPATH)));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + JIMMUNOL_HOST), Pattern.compile(CONTENT_SUBPATH)));
	}
	
	private static final Pattern PATTERN_FROM_URL = Pattern.compile(CONTENT_SUBPATH + "(.+?)\\.");
	private static final Pattern BIBTEX_PATTERN = Pattern.compile("<a.*href=\"([^\"]+)\".*>BibTeX</a>");
	
	private static final String DOWNLOAD_URL_CSHLP_HOST = getDownloadURLForHost(CSHLP_HOST, "cshperspect");
	private static final String DOWNLOAD_URL_JBC_HOST = getDownloadURLForHost(JBC_HOST, "jbc");
	private static final String DOWNLOAD_URL_CANCERRES_AACJOURNALS_HOST = getDownloadURLForHost(CANCERRES_AACJOURNALS_HOST, "canres");
	private static final String DOWNLOAD_URL_JIMMUNOL_HOST = getDownloadURLForHost(JIMMUNOL_HOST, "jimmunol");
	
	private static String getDownloadURLForHost(final String host, final String hostId) {
		return HTTP + host + "/citmgr?type=bibtex&gca=" + hostId + ";";
	}
	
	@Override
	protected String getDownloadURL(URL url, String cookies) throws ScrapingException {
		// FIXME: remove if else chain :(
		final Matcher m = PATTERN_FROM_URL.matcher(url.getPath());
		if (m.find()) {
			final String id = m.group(1);
			if (url.getHost().contains(CSHLP_HOST)) {
				return DOWNLOAD_URL_CSHLP_HOST + id;
			}
			
			if (url.getHost().contains(JBC_HOST)) {
				return DOWNLOAD_URL_JBC_HOST + id;
			}
			
			return getDownloadURLForHost(url,  cookies);
		}
		return null;
	}
	
	private static String getDownloadURLForHost(URL url, String cookies) throws ScrapingException{
		try {
			final String content = WebUtils.getContentAsString(url, cookies);
			final Matcher m = BIBTEX_PATTERN.matcher(content);
			if (m.find()) {
				if (url.getHost().contains(CANCERRES_AACJOURNALS_HOST)) {
					return HTTP + CANCERRES_AACJOURNALS_HOST + m.group(1);					
				}
				
				if (url.getHost().contains(JIMMUNOL_HOST)) {
					return HTTP + JIMMUNOL_HOST + m.group(1);					
				}
			}
		} catch (final IOException e) {
			throw new ScrapingException(e);
		}
		return null;
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
		return info;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}
}
