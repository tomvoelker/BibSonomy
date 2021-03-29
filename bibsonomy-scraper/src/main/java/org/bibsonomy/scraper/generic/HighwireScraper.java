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
package org.bibsonomy.scraper.generic;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.util.WebUtils;

/**
 * 
 */
public class HighwireScraper implements Scraper {
	private static final String SITE_NAME = "Highwire Scraper Collection";
	private static final String SITE_URL = "http://highwire.stanford.edu/lists/allsites.dtl";
	private static final String INFO 	= "This scraper parses a publication page from one of these <a href=\"http://highwire.stanford.edu/lists/allsites.dtl\">journals hosted by Highwire Press</a>  " +
			"and extracts the adequate BibTeX entry.";

	// e.g., /citmgr?gca=horttech%3B28%2F1%2F10"
	private static final Pattern URL_PATTERN_1 = Pattern.compile("/citmgr\\?gca=[^\"\']+");
	// e.g., /highwire/citation/14615/bibtext
	private static final Pattern URL_PATTERN_2 = Pattern.compile("/highwire/citation/\\d+/bibtext");
	// e.g., /paleobiol/downloadcitation/520315?format=bibtex"
	private static final Pattern URL_PATTERN_3 = Pattern.compile("/\\w+/downloadcitation/\\d+\\?format=bibtex");
	
	// patterns to identify and clean up bibtex keys (remove white space)
	private static final Pattern BIBTEX_KEY_PATTERN = Pattern.compile("@\\w+\\{.+,");
	private static final Pattern WHITE_SPACE_PATTERN = Pattern.compile("\\s");

	
	@Override
	public boolean scrape(final ScrapingContext sc) throws ScrapingException {
		if (sc.getUrl() != null) { //-- url shouldn't be null

			/*
			 * test if the export link is available: /cgi/citmgr?gca=abcd;999/99/99
			 * 
			 * If not, this scraper can't do anything and hence returns false. It does NOT 
			 * throw an exception because the IEScraper might do its job.
			 * 
			 */
			String pageContent;
			try {
				pageContent = sc.getPageContent();
			} catch (final ScrapingException e) {
				return false;
			}

			try {
				// extract URL path from HTML content
				final String urlPath = getUrlPath(pageContent);
				if (ValidationUtils.present(urlPath)) {
					sc.setScraper(this);

					//-- form the host url and put them together 
					final String newUrl = "http://" + sc.getUrl().getHost() + urlPath;

					//-- get the bibtex export and throw new ScrapingException if the url is broken
					String bibtex = WebUtils.getContentAsString(new URL(newUrl));

					// fix the BibTeX key FIXME: check if necessary
					bibtex = fixBibTeXKey(bibtex);


					//-- bibtex string may not be empty
					if (ValidationUtils.present(bibtex)) {
						sc.setBibtexResult(bibtex);
						return true;
					}

					throw new ScrapingFailureException("getting bibtex failed");
				}

			} catch (final IOException ex) {
				throw new InternalFailureException(ex);
			}
		}
		//-- This Scraper can`t handle the specified url
		return false;
	}

	/**
	 * Tries to find URL paths to BibTeX export in page content.
	 * 
	 * @param pageContent
	 * @return
	 */
	private static String getUrlPath(final String pageContent) {
		// try first pattern, e.g., /citmgr?gca=horttech%3B28%2F1%2F10"
		final Matcher m1 = URL_PATTERN_1.matcher(pageContent);
		if (m1.find()){
			//-- to export the bibtex we need to replace ? through ?type=bibtex
			return m1.group(0).replaceFirst("\\?","?type=bibtex&");
		} 
		// try next pattern, e.g., /highwire/citation/14615/bibtext
		final Matcher m2 = URL_PATTERN_2.matcher(pageContent);
		if (m2.find()) {
			return m2.group(0);
		}
		// e.g., /paleobiol/downloadcitation/520315?format=bibtex"
		final Matcher m3 = URL_PATTERN_3.matcher(pageContent);
		if (m3.find()) {
			return m3.group(0);
		}
		return null;
	}



	@Override
	public Collection<Scraper> getScraper() {
		return Collections.<Scraper>singletonList(this);
	}

	@Override
	public boolean supportsScrapingContext(final ScrapingContext sc) {
		if (ValidationUtils.present(sc.getUrl())) {
			try {
				return 
						URL_PATTERN_1.matcher(sc.getPageContent()).find() || 
						URL_PATTERN_2.matcher(sc.getPageContent()).find() ||
						URL_PATTERN_3.matcher(sc.getPageContent()).find();
			} catch (final ScrapingException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	@Override
	public String getInfo() {
		return INFO;
	}

	/**
	 * @return site name
	 */
	public String getSupportedSiteName(){
		return SITE_NAME;
	}


	/**
	 * @return site url
	 */
	public String getSupportedSiteURL(){
		return SITE_URL;
	}

	/**
	 * Need to fix the bibtexkey. Its necessary to replace
	 * ALL whitespace through underscores otherwise the import 
	 * will crash.
	 * FIXME: method looks complicated, can it be simplified?
	 */
	private static String fixBibTeXKey(final String bibtex) {
		//-- create the pattern to find the bibtexkey
		final Matcher ma1 = BIBTEX_KEY_PATTERN.matcher(bibtex);

		//-- for every match ...
		while(ma1.find()){
			final String bibtexpart = ma1.group(0);
			final Matcher mat = WHITE_SPACE_PATTERN.matcher(bibtexpart);
			// ... check if whitespaces are existing and replace 
			// them through underscore
			if (mat.find()){
				final String preparedbibtexkey = mat.replaceAll("_");
				return bibtex.replaceFirst(Pattern.quote(bibtexpart), preparedbibtexkey);
			}
		}
		return bibtex;
	}
}
