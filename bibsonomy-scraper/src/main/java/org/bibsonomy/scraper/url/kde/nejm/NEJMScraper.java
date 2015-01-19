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
package org.bibsonomy.scraper.url.kde.nejm;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;

/**
 * @author clemens
 */
public class NEJMScraper extends GenericBibTeXURLScraper {

	private static final String SITE_NAME = "The New England Journal of Medicine";
	private static final String SITE_URL = "http://www.nejm.org";
	private static final String INFO = "For references from the "+href(SITE_URL, SITE_NAME)+".";
	
	private static final String FORMAT_BIBTEX = "&format=bibtex";
	
	private static final List<Pair<Pattern, Pattern>> URL_PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "nejm.org"), AbstractUrlScraper.EMPTY_PATTERN));
	
	private static final Pattern pattern = Pattern.compile("doi/[^/]*/([^/]*/[^/#\\?]*)");
	

	@Override
	public String getInfo() {
		return INFO;
	}
	
	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return URL_PATTERNS;
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
		final Matcher matcher = pattern.matcher(url.toString());
		if (matcher.find()) {
			final String doi = matcher.group(1).replace("%2F", "/");
			final String downloadUrl = SITE_URL+"/action/downloadCitation?doi=" + doi + "&include=cit";
			return downloadUrl + FORMAT_BIBTEX;			
		}
		return null;
	}


}
