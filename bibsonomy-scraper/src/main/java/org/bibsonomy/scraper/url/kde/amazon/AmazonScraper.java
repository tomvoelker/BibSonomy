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
package org.bibsonomy.scraper.url.kde.amazon;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.scraper.url.kde.worldcat.WorldCatScraper;
import org.bibsonomy.util.WebUtils;
import org.bibsonomy.util.id.ISBNUtils;

/**
 * Scraper for the amazon onlineshop
 * 
 * @author tst
 */
public class AmazonScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "Amazon";
	private static final String SITE_URL = "http://www.amazon.com/";
	private static final String INFO = "Extracts publications from the "
			+ href(SITE_URL, SITE_NAME) + " onlineshop.";
	/**
	 * Supported AMAZON Hosts
	 */
	private static final String AMAZON_HOST_COM = "amazon.com";
	private static final String AMAZON_HOST_DE = "amazon.de";
	private static final String AMAZON_HOST_CA = "amazon.ca";
	private static final String AMAZON_HOST_FR = "amazon.fr";
	private static final String AMAZON_HOST_JP = "amazon.jp";
	private static final String AMAZON_HOST_CO_JP = "amazon.co.jp";
	private static final String AMAZON_HOST_CO_UK = "amazon.co.uk";
	private static final List<Pair<Pattern, Pattern>> patterns = new LinkedList<Pair<Pattern, Pattern>>();

	static {
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*"
				+ AMAZON_HOST_CA), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*"
				+ AMAZON_HOST_JP), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*"
				+ AMAZON_HOST_CO_JP), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*"
				+ AMAZON_HOST_CO_UK), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*"
				+ AMAZON_HOST_COM), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*"
				+ AMAZON_HOST_DE), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*"
				+ AMAZON_HOST_FR), AbstractUrlScraper.EMPTY_PATTERN));
	}

	private static final Pattern ISBN = Pattern.compile("ISBN (\\d+)");

	/**
	 * INFO field of this scraper
	 */
	@Override
	public String getInfo() {
		return INFO;
	}

	/**
	 * Scrapes a product from amazon
	 */
	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		
		// try to extract isbn and use the worldcat scraper
		try {
			final String content = WebUtils.getContentAsString(sc.getUrl().toString());
			final Matcher m = ISBN.matcher(content);
			final String isbn;
			if (m.find()) {
				isbn = m.group(1);
			} else {
				isbn = ISBNUtils.extractISBN(sc.getPageContent());
			}
			
			if (!present(isbn)) {
				return false;
			}
			
			final String bibtex = WorldCatScraper.getBibtexByISBNAndReplaceURL(isbn, sc.getUrl().toString());
			if (!present(bibtex)) {
				return false;
			}
			
			sc.setBibtexResult(bibtex);
			return true;
		} catch (IOException ex) {
			throw new ScrapingFailureException(ex);
		}
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