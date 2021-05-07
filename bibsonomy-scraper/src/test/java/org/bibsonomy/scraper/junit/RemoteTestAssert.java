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
package org.bibsonomy.scraper.junit;

import static org.bibsonomy.util.ValidationUtils.present;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.util.StringUtils;

import bibtex.dom.BibtexEntry;
import bibtex.dom.BibtexFile;
import bibtex.parser.BibtexParser;

/**
 * asserts for remote tests
 *
 * @author dzo
 */
public class RemoteTestAssert {

	/**
	 * calls the specified scraper with the provided url and tests the returned result of the scraper
	 * with the contents of the provided result file
	 * @param url
	 * @param scraperClass
	 * @param resultFile
	 */
	public static void assertScraperResult(final String url, final Class<? extends Scraper> scraperClass, final String resultFile) {
		assertScraperResult(url, null, scraperClass, resultFile);
	}

	/**
	 * calls the specified scraper with the 
	 * @param url
	 * @param selection
	 * @param scraperClass
	 * @param resultFile
	 */
	public static void assertScraperResult(final String url, final String selection, final Class<? extends Scraper> scraperClass, final String resultFile) {
		try {
			final Scraper scraper = createScraper(scraperClass);
			final ScrapingContext scrapingContext = createScraperContext(url, selection);
			scraper.scrape(scrapingContext);
			final String bibTeXResult = scrapingContext.getBibtexResult();
			
			if (bibTeXResult != null){
				final BibtexParser parser = new BibtexParser(true);
				final BibtexFile bibtexFile = new BibtexFile();
				final BufferedReader sr = new BufferedReader(new StringReader(bibTeXResult));
				// parse source
				parser.parse(bibtexFile, sr);
				/*
				 * final check if bibtex is valid, at least one bibtexentry should be in the bibtexFile
				 */
				final boolean bibtexValid = bibtexFile.getEntries().stream().anyMatch(BibtexEntry.class::isInstance);

				// test if expected bib is equal to scraped bib (which must be valid bibtex) 
				assertThat("scraped BibTeX not valid", bibtexValid, is(true));
				final String expectedReference = normBibTeX(getExpectedBibTeX(resultFile));
				assertThat(normBibTeX(bibTeXResult), is(expectedReference));
			} else {
				fail("nothing scraped");
			}
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static String normBibTeX(final String bibTeX) {
		if (!present(bibTeX)) {
			return bibTeX;
		}
		return bibTeX.replaceAll("\\r\\n", "\n").trim();
	}

	/**
	 * @param resultFile
	 * @return
	 */
	private static String getExpectedBibTeX(String resultFile) throws IOException {
		try (final InputStream in = RemoteTestAssert.class.getClassLoader().getResourceAsStream("org/bibsonomy/scraper/data/" + resultFile)) {
			return StringUtils.getStringFromReader(new BufferedReader(new InputStreamReader(in, StringUtils.DEFAULT_CHARSET)));
		}
	}

	private static Scraper createScraper(final Class<? extends Scraper> scraperClass) throws InstantiationException, IllegalAccessException {
		return scraperClass.newInstance();
	}
	
	private static ScrapingContext createScraperContext(final String url, final String selection) throws MalformedURLException {
		final URL testURL;
		if (present(url)) {
			testURL = new URL(url);
		} else {
			testURL = null;
		}
		final ScrapingContext testSC = new ScrapingContext(testURL);
		if (selection != null) {
			testSC.setSelectedText(selection);
		}
		return testSC;
	}
}
