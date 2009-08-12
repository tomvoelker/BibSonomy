/**
 *  
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *   
 *  Copyright (C) 2006 - 2008 Knowledge & Data Engineering Group, 
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

import java.net.URL;
import java.util.List;

import junit.framework.TestResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import org.bibsonomy.scraper.URLTest.URLScraperUnitTest;
import org.bibsonomy.scraper.importer.IUnitTestImporter;
import org.bibsonomy.scraper.importer.xml.XMLUnitTestImporter;


/**
 * Main class which starts the scraper unit test.
 * @author tst
 *
 */
public class UnitTestRunner {

	private static final String LINE = "------------------------------------------------------------------------";

	private Log log = LogFactory.getLog(UnitTestRunner.class);

	/**
	 * Importer which reads the tests from a external sources.
	 */
	private IUnitTestImporter importer = null;

	/**
	 * Init the Importer
	 */
	public UnitTestRunner(){
		// importer for xml + bib file sources
		importer = new XMLUnitTestImporter();
	}

	/**
	 * This Method reads and runs the unit tests.
	 */
	public void run(){
		try {
			if(importer == null)
				throw new Exception("no UnitTestImporter available");

			final List<ScraperUnitTest> unitTests = importer.getUnitTests();

			int error = 0;
			for (final ScraperUnitTest test : unitTests){
				if (test.isEnabled()) {
					final TestResult result = test.run();
					test.setTestResult(result);
					if (test.isTestFailed())	error++;
				}
			}

			log.info(LINE);
			log.info("Tests run: " + unitTests.size() + ", Failures: " + error);
			log.info("");

			if (error > 0) {
				log.warn("FAILED tests:");
				log.warn("");

				for (final ScraperUnitTest test : unitTests) {
					if (test.isTestFailed()) {
						log.warn("\tTests run: " + test.getScraperClass().getName() + ":" + test.getScraperTestId() );
					}
				}
				log.warn("");
				log.warn(LINE);
				log.warn("Details:");
				for (final ScraperUnitTest test : unitTests) {
					if (test.isTestFailed()) {
						test.printTestFailure();
					}
				}
			}




		} catch (final Exception e) {
			ParseFailureMessage.printParseFailureMessage(e, "main class");
		}
	}

	/**
	 * Runs a single URLSCraperUnitTest, which ist referenced by its ID.
	 * @param testId ID from URL test
	 * @return result of the test
	 */
	public boolean runSingleTest(String testId){
		URL log4j = new UnitTestRunner().getClass().getResource("log4j.properties");
		PropertyConfigurator.configure(log4j);
		try {
			if(importer == null)
				throw new Exception("no UnitTestImporter available");

			List<ScraperUnitTest> unitTests = importer.getUnitTests();

			for(ScraperUnitTest test : unitTests){
				if(test.getScraperTestId().equals(testId)){
					TestResult result = test.run();
					test.setTestResult(result);
					if(result.errorCount() > 0 || result.failureCount() > 0) {
						test.printTestFailure();
						return false;
					}
					else
						return true;
				}
			}

		} catch (Exception e) {
			ParseFailureMessage.printParseFailureMessage(e, "main class");
		}
		return false;
	}

	/**
	 * Runs a single URLSCraperUnitTest, which ist referenced by its ID and returns test and its results.
	 * @param testId ID from URL test
	 * @return scraped bibtex, null if scraping failed
	 */
	public URLScraperUnitTest getUrlUnitTest(String testId){
		URL log4j = new UnitTestRunner().getClass().getResource("log4j.properties");
		PropertyConfigurator.configure(log4j);
		try {
			if(importer == null)
				throw new Exception("no UnitTestImporter available");

			List<ScraperUnitTest> unitTests = importer.getUnitTests();

			for(ScraperUnitTest test : unitTests){
				if(test.getScraperTestId().equals(testId)){
					TestResult result = test.run();
					if(result.errorCount() > 0 || result.failureCount() > 0)
						return (URLScraperUnitTest)test;
					else
						return (URLScraperUnitTest)test;
				}
			}

		} catch (Exception e) {
			ParseFailureMessage.printParseFailureMessage(e, "main class");
		}
		return null;
	}

	/**
	 * starts the whole party
	 * @param args not needed
	 */
	public static void main(String[] args){
		new UnitTestRunner().run();
	}

}
