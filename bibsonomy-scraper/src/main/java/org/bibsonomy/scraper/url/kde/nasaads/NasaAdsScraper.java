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
package org.bibsonomy.scraper.url.kde.nasaads;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.ExamplePrototype;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;
import org.bibsonomy.util.UrlUtils;

/**
 * Scraper for NASA ADS.
 * Collects BibTeX snippets and single references (HTML page or BibTeX page).
 *   
 * @author rja
 */
public class NasaAdsScraper extends GenericBibTeXURLScraper implements ExamplePrototype {

	private static final String SITE_NAME = "The SAO/NASA Astrophysics Data System";
	private static final String SITE_HOST = "adsabs.harvard.edu";
	private static final String SITE_URL = "http://" + SITE_HOST + "/";

	private static final String INFO = "Extracts publications from " + href(SITE_URL, SITE_NAME) + 
			". Publications can be extracted as a selected BibTeX snippet (one or more publications) or by the URL of a single reference.";

	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + SITE_HOST), AbstractUrlScraper.EMPTY_PATTERN));

	@Override
	public String getInfo() {
		return INFO;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.generic.AbstractGenericFormatURLScraper#getDownloadURL(java.net.URL, java.lang.String)
	 */
	@Override
	protected String getDownloadURL(final URL url, final String cookies) throws ScrapingException, IOException {
		final String path = url.getPath();
		if (path.startsWith("/abs/")) {
			final String id = path.substring(5);
			
			return SITE_URL + "/cgi-bin/nph-bib_query?bibcode=" + UrlUtils.safeURIDecode(id) + "&data_type=BIBTEX&db_key=AST&nocookieset=1";
		} else if (url.getQuery().toLowerCase().contains("data_type=bibtex")) {
			return url.toExternalForm();
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

}
