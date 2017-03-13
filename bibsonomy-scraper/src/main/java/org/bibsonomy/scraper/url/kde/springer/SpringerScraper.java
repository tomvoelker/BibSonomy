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
package org.bibsonomy.scraper.url.kde.springer;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.scraper.url.kde.worldcat.WorldCatScraper;
import org.bibsonomy.util.id.ISBNUtils;

/**
 * Scraper for springer.com
 * 
 * @author tst
 */
public class SpringerScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "Springer";
	private static final String SITE_URL = "http://www.springer.com/";
	private static final String INFO = "Scraper for books from " + href(SITE_URL, SITE_NAME) + ".";

	/** Host */
	private static final String HOST = "springer.com";
	private static final String SPRINGER_CITATION_HOST = "link.springer.com/book";

	private static final List<Pair<Pattern, Pattern>> patterns = new LinkedList<Pair<Pattern, Pattern>>();
	static {
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + SPRINGER_CITATION_HOST), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + HOST + "$"), AbstractUrlScraper.EMPTY_PATTERN));
	}
	
	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);

		try {
			final String url = sc.getUrl().toString();
			final String isbn = ISBNUtils.extractISBN(url);
			final String bibtex = WorldCatScraper.getBibtexByISBN(isbn);

			if (present(bibtex)) {
				sc.setBibtexResult(bibtex);
				return true;
			} 
			
			throw new ScrapingFailureException("getting bibtex failed");

		} catch (IOException ex) {
			throw new InternalFailureException(ex);
		}
	}
	
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
}
