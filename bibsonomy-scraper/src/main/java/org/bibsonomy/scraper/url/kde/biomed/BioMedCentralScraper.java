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
package org.bibsonomy.scraper.url.kde.biomed;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.ExamplePrototype;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;

/**
 * @author rja
 */
public class BioMedCentralScraper extends GenericBibTeXURLScraper implements ExamplePrototype {

	private static final String SITE_NAME = "BioMed Central";
	private static final String SITE_HOST = "biomedcentral.com";
	private static final String SITE_URL  = "http://" + SITE_HOST + "/";
	private static final Pattern PATH_PATTERN = Pattern.compile("/articles/(.+)(\\?.*)?$");

	private static final String INFO = "This scraper parses a publication from " + href(SITE_URL, SITE_NAME)+".";
	private static final List<Pair<Pattern, Pattern>> PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + SITE_HOST), Pattern.compile("/articles/.*")));

	@Override
	public String getInfo() {
		return INFO;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.generic.AbstractGenericFormatURLScraper#getDownloadURL(java.net.URL, java.lang.String)
	 */
	@Override
	protected String getDownloadURL(final URL url, final String cookies) throws ScrapingException, IOException {
		/*
		 * input:  http://jbiomedsem.biomedcentral.com/articles/10.1186/2041-1480-1-S1-S6
		 * output: http://citation-needed.springer.com/v2/references/10.1186/2041-1480-1-S1-S6?format=bibtex&flavour=citation
		 */
		final Matcher m = PATH_PATTERN.matcher(url.getPath());
		if (m.find()) {
			return "http://citation-needed.springer.com/v2/references/" + m.group(1) + "?format=bibtex&flavour=citation";
		}
		return null;
	}
	
	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
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
