/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package org.bibsonomy.scraper.url.kde.opac;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.PicaToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;

/**
 * @author C. Kramer
 */
public class OpacScraper extends AbstractUrlScraper {
	private static final String SITE_URL = "http://opac.bibliothek.uni-kassel.de/";
	private static final String HOST_NAME = "opac.bibliothek.uni-kassel.de";
	private static final String SITE_NAME = "Bibliothek Kassel";
	private static final String info = "This scraper parses a publication page from " + href(SITE_URL , SITE_NAME);

	/**
	 * TODO: this scraper match only on URL's with es specific query value in path and queries. The current patterns don't work.
	 */
	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(HOST_NAME + ".*"), Pattern.compile(".*(/PPN.|TRM=[0-9]+)*")));
	
	@Override
	protected boolean scrapeInternal(final ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);

		try {
			// create a converter and start converting :)
			final PicaToBibtexConverter converter = new PicaToBibtexConverter("xml", sc.getUrl().toString());

			final String bibResult = converter.toBibtex(sc.getPageContent());

			if (bibResult != null) {
				sc.setBibtexResult(bibResult);
				return true;
			}
			
			throw new ScrapingFailureException("getting bibtex failed");
		} catch (final ScrapingException e){
			throw new InternalFailureException(e);
		}
	}
	
	@Override
	public String getInfo() {
		return info;
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
