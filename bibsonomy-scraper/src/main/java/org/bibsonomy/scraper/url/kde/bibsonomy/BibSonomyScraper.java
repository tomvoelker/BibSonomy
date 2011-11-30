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

package org.bibsonomy.scraper.url.kde.bibsonomy;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;


/**
 * TODO: add support for all PUMA instances
 * 
 * Scraper for single publications from bibsonomy.org.
 * 
 * @author tst
 * @version $Id$
 */
public class BibSonomyScraper extends AbstractUrlScraper {
	private static final String SITE_NAME = "BibSonomy";
	private static final String SITE_URL = "http://www.bibsonomy.org";
	private static final String INFO = "If you don't like the copy button from " + href(SITE_URL, SITE_NAME) + ", use your postPublication button.";

	private static final String BIBSONOMY_HOST = "bibsonomy.org";
	private static final String BIBTEX_PUBLICATION_PATH_PATTERN = "/[bib/]?[bibtex|publication].*";
	
	private static final String BIBTEX_FORMAT_PATH_PREFIX = "/bib";

	private static final List<Tuple<Pattern,Pattern>> patterns = Collections.singletonList(
		new Tuple<Pattern, Pattern>(Pattern.compile(".*" + BIBSONOMY_HOST), Pattern.compile(BIBTEX_PUBLICATION_PATH_PATTERN))
	);
	
	/**
	 * Scrapes only single publications from bibsonomy.org/[bibtex|publication] and bibsonomy.org/bib/[bibtex|publication]
	 */
	@Override
	protected boolean scrapeInternal(final ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		try {
			final String path = sc.getUrl().getPath();
			String url = sc.getUrl().toString();
			// if /bibtex page or /publication page then change path to add /bib as prefix and download
			if (!path.startsWith(BIBTEX_FORMAT_PATH_PREFIX + "/")) {
				url = SITE_URL + BIBTEX_FORMAT_PATH_PREFIX + path;
			}
			
			final String bibResult = WebUtils.getContentAsString(url);
			if (present(bibResult)) {
				sc.setBibtexResult(bibResult);
				return true;
			}
			
			throw new ScrapingFailureException("getting bibtex failed");
		} catch (final IOException ex) {
			throw new InternalFailureException(ex);
		}
	}

	public String getInfo() {
		return INFO;
	}

	@Override
	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	public String getSupportedSiteURL() {
		return SITE_URL;
	}
}
