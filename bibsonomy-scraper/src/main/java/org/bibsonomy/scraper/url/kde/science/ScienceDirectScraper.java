/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.scraper.url.kde.science;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;
import org.bibsonomy.util.WebUtils;

/**
 * Scraper for ScienceDirect.
 * 
 * @author rja
 *
 */
public class ScienceDirectScraper extends GenericBibTeXURLScraper {
	private static final Log log = LogFactory.getLog(ScienceDirectScraper.class);
	
	private static final String SCIENCE_CITATION_HOST     = "sciencedirect.com";
	
	private static final String SITE_NAME = "ScienceDirect";
	private static final String SITE_URL = "http://www." + SCIENCE_CITATION_HOST;
	private static final String info = "This scraper parses a publication page from " + href(SITE_URL, SITE_NAME)+".";
	private static final String SCIENCE_CITATION_PATH     = "/science";

	private static final String end = "zone=exportDropDown&citation-type=BIBTEX&export=Export&format=cite-abs";
	private static final Pattern PATTERN_FORM = Pattern.compile("<form name=\"exportCite\" method=post action=(.*) target=\"\">");
	
	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + SCIENCE_CITATION_HOST), Pattern.compile(SCIENCE_CITATION_PATH + ".*")));


	@Override
	public String getInfo() {
		return info;
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
	public String getDownloadURL(URL url) throws ScrapingException {
		String download_link = "";
		try{
			Matcher m = PATTERN_FORM.matcher(WebUtils.getContentAsString(url));
			if (m.find()){
				download_link = m.group(1);
			}
			return "http://" + url.getHost().toString() + download_link + end;
		} catch(IOException e) {
			log.error("error while getting download url for " + url, e);
		}
		return null;
	}

}
