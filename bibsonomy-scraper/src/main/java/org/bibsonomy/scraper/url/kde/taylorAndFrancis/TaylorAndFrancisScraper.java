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

package org.bibsonomy.scraper.url.kde.taylorAndFrancis;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * @author schwass
 * @version $Id$
 */
public class TaylorAndFrancisScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "Taylor & Francis Online";
	private static final String SITE_URL = "http://www.tandfonline.com/";
	private static final String INFO = "This scraper parses a publication page from " + href(SITE_URL, SITE_NAME)+".";

	private static final String TANDF_HOST_NAME = "tandfonline.com";
	
	private static final List<Pair<Pattern, Pattern>> PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + TANDF_HOST_NAME), AbstractUrlScraper.EMPTY_PATTERN));

	private static final Pattern DOI_PATTERN = Pattern.compile("/10\\.1080/\\d+(\\.\\d+)*$");
	
	private static final String TANDF_BIBTEX_DOWNLOAD_PATH = "action/downloadCitation";
	private static final String DOWNLOADFILENAME = "tandf_rajp2080_124";
	
	private static String postContent(String doi) {
		return "doi=" + doi
		+ "&downloadFileName=" + DOWNLOADFILENAME
		+ "&format=bibtex&direct=true&include=includeCit";
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
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
	}

	@Override
	protected boolean scrapeInternal(ScrapingContext scrapingContext) throws ScrapingException {
		scrapingContext.setScraper(this);
		Matcher matcher = DOI_PATTERN.matcher(scrapingContext.getUrl().toString());
		if (!matcher.find()) return false;
		try {
			final String cookie = WebUtils.getCookies(scrapingContext.getUrl());
			String bibtexEntry = WebUtils.getPostContentAsString(cookie, new URL(SITE_URL + TANDF_BIBTEX_DOWNLOAD_PATH), postContent(matcher.group().substring(1)));
			if (bibtexEntry != null) {
				scrapingContext.setBibtexResult(bibtexEntry.trim());
				return true;
			} else
				throw new ScrapingFailureException("getting BibTeX failed");
		} catch (IOException ex) {
			throw new ScrapingException(ex);
		}
	}

}
