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

package org.bibsonomy.scraper;

import static org.bibsonomy.util.ValidationUtils.present;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.importer.IUnitTestImporter;
import org.bibsonomy.scraper.importer.xml.XMLUnitTestImporter;

import bibtex.dom.BibtexEntry;
import bibtex.dom.BibtexFile;
import bibtex.parser.BibtexParser;


/**
 * helper class to start a scraper test.
 * @author tst
 */
public class UnitTestRunner {
	private static final Log log = LogFactory.getLog(UnitTestRunner.class);
	/** Importer which reads the tests from a external sources. */
	private static IUnitTestImporter IMPORTER = new XMLUnitTestImporter();
	
	/**
	 * runs a scraper test
	 * 
	 * @param testId ID from URL test
	 */
	public static void runSingleTest(final String testId) {
		try {
			final ScraperTestData testData = IMPORTER.getUnitTests().get(testId);
			if (present(testData)) {
				if (testData.isEnabled()) {
					try {
						final String bibTeXResult = callScraper(testData);
						/*
						 * final check if bibtex is valid, if not so
						 */
						boolean bibtexValid = false;
						
						if (bibTeXResult != null){
							final BibtexParser parser = new BibtexParser(true);
							final BibtexFile bibtexFile = new BibtexFile();
							final BufferedReader sr = new BufferedReader(new StringReader(bibTeXResult));
							// parse source
							parser.parse(bibtexFile, sr);
							
							for (Object potentialEntry:bibtexFile.getEntries())
								if ((potentialEntry instanceof BibtexEntry))
									bibtexValid = true;
							// test if expected bib is equal to scraped bib (which must be valid bibtex) 
							assertTrue("scraped BibTeX not valid", bibtexValid);
							final String expectedRefrence = testData.getExpectedBibTeX().trim();
							assertEquals(expectedRefrence, bibTeXResult.trim());
						} else {
							fail("nothing scraped");
						}
					} catch (Exception e) {
						fail(e.getMessage());
					}
				} else {
					log.warn("Scrapertest with id " + testData.getTestId() + " is disabled in XML Configuration.");
				}
			} else {
				fail("no scraper test with id " + testId + " found.");
			}
		} catch (final Exception e) {
			ParseFailureMessage.printParseFailureMessage(e, "main class");
			fail("parse failure");
		}
	}
	
	/**
	 * 
	 * @param testData
	 * @return the scraped BibTeX
	 * @throws MalformedURLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws ScrapingException
	 */
	public static String callScraper(final ScraperTestData testData) throws MalformedURLException, InstantiationException, IllegalAccessException, ClassNotFoundException, ScrapingException {
		// create scraping context based on XML data
		final ScrapingContext testSC = createScraperContext(testData);
		
		// scrape
		final Scraper scraper = createScraper(testData);
		scraper.scrape(testSC);
		return testSC.getBibtexResult();
	}

	protected static Scraper createScraper(final ScraperTestData testData) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		return (Scraper) Class.forName(testData.getScraperClassName()).newInstance();
	}

	protected static ScrapingContext createScraperContext(final ScraperTestData testData) throws MalformedURLException {
		final String url = testData.getUrl();
		final URL testURL;
		if (present(url)) {
			testURL = new URL(url);
		} else {
			testURL = null;
		}
		final ScrapingContext testSC = new ScrapingContext(testURL);
		final String selection = testData.getSelection();
		if (selection != null) {
			testSC.setSelectedText(selection);
		}
		return testSC;
	}
}
