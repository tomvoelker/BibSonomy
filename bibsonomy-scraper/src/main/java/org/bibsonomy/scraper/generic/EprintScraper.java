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

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;

/**
 * Scraper for repositories which use eprint
 * 
 * @author tst
 */
public class EprintScraper implements Scraper {
	/** pattern identifying eprints pages */
	private static final Pattern PATTERN = Pattern.compile("<\\s*link(?=.*rel=\"alternate\")(?=.*href=\"(http://.*eprint.*bib)\")(?=.*type=\"text/plain\")(?=.*title=\"BibTeX\").*>");

	private static final String INFO = "Scraper for repositories which use " + AbstractUrlScraper.href("http://www.eprints.org/", "eprints");
	private static final String SITE_NAME = "EprintsScraper";
	private static final String SITE_URL = "http://www.eprints.org/";

	@Override
	public String getInfo() {
		return INFO;
	}

	@Override
	public Collection<Scraper> getScraper() {
		return Collections.<Scraper> singleton(this);
	}

	@Override
	public boolean scrape(final ScrapingContext scrapingContext) throws ScrapingException {
		if (!present(scrapingContext.getUrl())) {
			return false;
		}
		// get the page content to find the bibtex url
		final String page = scrapingContext.getPageContent(); 
		final Matcher matcher = PATTERN.matcher(page);
		if (matcher.find()) {
			try {
				//get the URL to the bibtex
				final String bibtexLink = matcher.group(1);
				
				if (present(bibtexLink)) {
					// download the bibtex file
					final String bibtexResult = WebUtils.getContentAsString(bibtexLink);
					
					if (present(bibtexResult)) {
						// set scraper found
						scrapingContext.setScraper(this);
						scrapingContext.setBibtexResult(bibtexResult);
						return true;
					}
					return false;
				}
				return false;
			} catch (final IOException ex) {
				throw new InternalFailureException(ex);
			}
		}

		return false;
	}

	@Override
	public boolean supportsScrapingContext(final ScrapingContext scrapingContext) {
		// the eprint scraper needs a url
		if (!present(scrapingContext.getUrl())) {
			return false;
		}
		try {
			final String page = scrapingContext.getPageContent(); 
			// check whether page has got an eprint bibtex link or not
			return PATTERN.matcher(page).find();
		} catch (final ScrapingException ex) {
			return false;
		}
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

}
