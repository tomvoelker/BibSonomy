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

import java.util.Map;

import junit.framework.TestResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.scraper.URLTest.URLScraperUnitTest;
import org.bibsonomy.scraper.importer.IUnitTestImporter;
import org.bibsonomy.scraper.importer.xml.XMLUnitTestImporter;


/**
 * Main class which starts the scraper unit test.
 * @author tst
 *
 */
public class UnitTestRunner {

	/**
	 * Importer which reads the tests from a external sources.
	 */
	private static IUnitTestImporter IMPORTER = new XMLUnitTestImporter();
	
	private static final Log log = LogFactory.getLog(UnitTestRunner.class);

	/**
	 * Runs a single URLSCraperUnitTest, which is referenced by its ID.
	 * 
	 * @param testId ID from URL test
	 * @return result of the test
	 */
	public static boolean runSingleTest(String testId) {

		try {
			if (IMPORTER == null)
				throw new Exception("no UnitTestImporter available");

			final Map<String, ScraperUnitTest> unitTests = IMPORTER.getUnitTests();
			final ScraperUnitTest test = unitTests.get(testId);
			if (present(test)) {
				if(test.isEnabled()) {
					final TestResult result = test.run();
					test.setTestResult(result);
					if (result.errorCount() > 0 || result.failureCount() > 0) {
						test.printTestFailure();
						return false;
					}
				} else {
					log.warn("Scrapertest with id " + test.getScraperTestId() + " is disabled in XML Configuration.");
				}
				return true;
			}
		} catch (final Exception e) {
			ParseFailureMessage.printParseFailureMessage(e, "main class");
		}
		return false;
	}

	/**
	 * Runs a single URLSCraperUnitTest, which is referenced by its ID and returns test and its results.
	 * @param testId ID from URL test
	 * @return scraped bibtex, null if scraping failed
	 */
	public static URLScraperUnitTest getUrlUnitTest(final String testId) {
		try {
			if (IMPORTER == null)
				throw new Exception("no UnitTestImporter available");

			final Map<String, ScraperUnitTest> unitTests = IMPORTER.getUnitTests();
			final ScraperUnitTest test = unitTests.get(testId);
			if (present(test)){
				/*
				 * run test
				 */
				test.run();
				/*
				 * return test
				 */
				return (URLScraperUnitTest)test;
			}
		} catch (final Exception e) {
			ParseFailureMessage.printParseFailureMessage(e, "main class");
		}
		return null;
	}

}
