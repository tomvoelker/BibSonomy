/**
 *
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 *  Copyright (C) 2006 - 2011 Knowledge & Data Engineering Group,
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

package org.bibsonomy.scraper.url.kde.googlebooks;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * @author clemens
 * @version $Id$
 */
public class GoogleBooksScraper extends AbstractUrlScraper {

	private static final String SITE_URL  = "http://books.google.com/";
	private static final String SITE_NAME = "Google Books";
	private static final String INFO      = "Scrapes BibTeX from " + href(SITE_URL, SITE_NAME) + ".";

	private static final String HOST = "books.google.";
	private static final String PATH = "/books";
	
	private static final Pattern ID_PATTERN = Pattern.compile("id=(.*)&");
	
	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + HOST + ".*"), Pattern.compile(PATH + ".*")));
	
	@Override
	protected boolean scrapeInternal(final ScrapingContext sc)throws ScrapingException {
		sc.setScraper(this);
		try {
			final String downloadLink;
			
			// extract id from url
			final Matcher idMatcher = ID_PATTERN.matcher(sc.getUrl().toString());
			
			if(idMatcher.find()) {
				downloadLink = "http://" + sc.getUrl().getHost() + PATH + "/download/?id=" + idMatcher.group(1) + "&output=bibtex";
			} else {
				throw new ScrapingFailureException("id is not available");
			}
			// download bibtex
			final String bibtex = WebUtils.getContentAsString(new URL(downloadLink));
			if (bibtex != null) {
				sc.setBibtexResult(bibtex);
				return true;
			}
			return false;
		} catch (IOException ex) {
			throw new InternalFailureException(ex);
		}		
	}

	public String getInfo() {
		return INFO;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	public String getSupportedSiteURL() {
		return SITE_URL;
	}
}
