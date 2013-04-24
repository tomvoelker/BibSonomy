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

package org.bibsonomy.scraper.generic;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;
import static org.bibsonomy.util.ValidationUtils.present;

/**
 * Scraper for repositories which use eprint
 * 
 * @author tst
 * @version $Id$
 */
public class EprintScraper implements Scraper {

	private static final String INFO = "Scraper for repositories which use " + AbstractUrlScraper.href("http://www.eprints.org/", "eprints");

	/*
	 * pattern identifying eprints pages
	 */
	private static final Pattern PATTERN = Pattern.compile("rel=\"alternate\".*href=\"http://.*eprint.*\".*title=\"BibTeX\"");
	/*
	 * pattern identifying the bibtex-url
	 */
	private static final Pattern BIBTEX_URL = Pattern.compile("href=\"http://.*bib");


	@Override
	public String getInfo() {
		return INFO;
	}

	@Override
	public Collection<Scraper> getScraper() {
		return Collections.<Scraper> singleton(this);
	}

	@Override
	public boolean scrape(ScrapingContext scrapingContext) throws ScrapingException {
		//set scraper found
		scrapingContext.setScraper(this);

		//get the page content to find the bibtex url
		final String page = scrapingContext.getPageContent(); 
		Matcher matcher = PATTERN.matcher(page);
		
		if(matcher.find()) {
			try {
				
				String bibtexLink = "";
				Matcher bibtexMatcher = BIBTEX_URL.matcher(page.substring(matcher.start(), matcher.end()));
				
				//find the href attribute to identify the bibtex url
				if( bibtexMatcher.find() ) {
					
					bibtexLink = bibtexMatcher.group().split("=")[1];
					bibtexLink = bibtexLink.substring(1);
					
				} else {
					
					return false;
					
				}
				
				if ( present(bibtexLink) ) {
					
					//set the bibtex result
					String bibtexResult = WebUtils.getContentAsString(bibtexLink);
					
					if (present(bibtexResult)) {
						
						scrapingContext.setBibtexResult(bibtexResult);
						return true;
						
					} else {
						
						return false;
						
					}
					
				} else {
					
					return false;
					
				}
				
			} catch (IOException ex) {
				return false;
			}
		}

		return false;
	}

	@Override
	public boolean supportsScrapingContext(ScrapingContext scrapingContext) {
		try {
			final String page = scrapingContext.getPageContent(); 
			//check wether page has got an eprint bibtex link or not
			return PATTERN.matcher(page).find();
		} catch (ScrapingException ex) {
			return false;
		}
	}

}
