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

package org.bibsonomy.scraper.url.kde.bibsonomy;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;


/**
 * TODO: add support for all PUMA instances
 * 
 * Scraper for single publications from bibsonomy.org.
 * 
 * @author tst
 */
public class BibSonomyScraper extends GenericBibTeXURLScraper {
	private static final String SITE_NAME = "BibSonomy";
	private static final String SITE_URL = "http://www.bibsonomy.org";
	private static final String INFO = "If you don't like the copy button from " + href(SITE_URL, SITE_NAME) + ", use your postPublication button.";

	private static final String BIBSONOMY_HOST = "bibsonomy.org";
	private static final String BIBTEX_PUBLICATION_PATH_PATTERN = "/[bib/]?[bibtex|publication].*";
	
	private static final String BIBTEX_FORMAT_PATH_PREFIX = "/bib";

	private static final List<Pair<Pattern,Pattern>> patterns = Collections.singletonList(
		new Pair<Pattern, Pattern>(Pattern.compile(".*" + BIBSONOMY_HOST), Pattern.compile(BIBTEX_PUBLICATION_PATH_PATTERN))
	);
	


	@Override
	public String getInfo() {
		return INFO;
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
		final String path = url.getPath();
		// if /bibtex page or /publication page then change path to add /bib as prefix and download
		if (!path.startsWith(BIBTEX_FORMAT_PATH_PREFIX + "/")) {
			return  SITE_URL + BIBTEX_FORMAT_PATH_PREFIX + path;
		}
		return url.toExternalForm();
	}
}
