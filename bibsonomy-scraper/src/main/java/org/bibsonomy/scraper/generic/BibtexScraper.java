/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;

import bibtex.dom.BibtexEntry;
import bibtex.dom.BibtexFile;
import bibtex.parser.BibtexParser;

/**
 * Search in sourcecode from the given page for BibTeX and scrape it.
 * 
 * @author tst
 */
public class BibtexScraper implements Scraper {

	private final static Pattern invalidChar = Pattern.compile("[^\\p{L}\\p{Nd}\\p{Punct}\\p{Space}]+");
	private static final String INFO = "Scraper for BibTeX, independent from URL.";

	@Override
	public String getInfo() {
		return INFO;
	}

	@Override
	public Collection<Scraper> getScraper() {
		return Collections.<Scraper>singletonList(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.scraper.Scraper#scrape(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean scrape(final ScrapingContext sc) throws ScrapingException {
		if ((sc != null) && (sc.getUrl() != null)) {
			final String result = parseBibTeX(sc.getPageContent());
			if (result != null) {
				//Matcher m = invalidChar.matcher(result);
				//if (!m.find()) {
					sc.setScraper(this);
					sc.setBibtexResult(result);
					return true;
				//}
			}
		}
		return false;
	}

	private static String parseBibTeX(final String pageContent) {
		if (pageContent == null) {
			return null;
		}

		// html clean up
		final String source = StringEscapeUtils.unescapeHtml(pageContent).replaceAll("<\\s*+br\\s*+/?>", "\n")
				//this should remove the remaining html tags
				.replaceAll("</?\\s*+\\w++.*?>", "");

		try {

			/* 
			 * copied from SnippetScraper
			 */
			final BibtexParser parser = new BibtexParser(false);
			final BibtexFile bibtexFile = new BibtexFile();
			final BufferedReader sr = new BufferedReader(new StringReader(source));
			// parse source
			parser.parse(bibtexFile, sr);

			for (final Object potentialEntry : bibtexFile.getEntries()) {
				if ((potentialEntry instanceof BibtexEntry)) {
					sr.close();
					return potentialEntry.toString();
				}
			}
			sr.close();
		} catch (final Exception ex) {
			/*
			 * be silent
			 * This scraper shall not throw any exceptions, since it shall just
			 * check, if the given page contains bibtex or not. If scraping is 
			 * not possible, fail silently.
			 */
		}
		return null;
	}

	@Override
	public boolean supportsScrapingContext(final ScrapingContext sc) {
		if ((sc != null) && (sc.getUrl() != null)) {
			try {
				return parseBibTeX(sc.getPageContent()) != null;
			} catch (final InternalFailureException ex) {
				return false;
			} catch (final ScrapingException ex) {
				return false;
			}
		}
		return false;
	}

	/**
	 * @return site name
	 */
	public String getSupportedSiteName(){
		return null;
	}

	/**
	 * @return site url
	 */
	public String getSupportedSiteURL(){
		return null;
	}
}
