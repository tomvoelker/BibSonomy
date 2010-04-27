/**
 *
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 *  Copyright (C) 2006 - 2010 Knowledge & Data Engineering Group,
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

import junit.framework.TestCase;
import junit.framework.TestResult;

/**
 * Basic data structure for scraper unit tests.
 * All test types must derive this class.
 * @author tst
 *
 */
public abstract class ScraperUnitTest extends TestCase{

	protected TestResult testResult;
	
	protected Scraper scraper;
	
	public TestResult getTestResult() {
		return this.testResult;
	}

	public void setTestResult(TestResult testResult) {
		this.testResult = testResult;
	}

	public boolean isTestFailed(){
		if(testResult != null && (testResult.errorCount() > 0 || testResult.failureCount() > 0))
			 return true;
		else
			return false;
	}
	
	/**
	 * Tells super which name has the method which has to be tested.
	 * @param testMethod String representation of the testing method 
	 */
	public ScraperUnitTest(String testMethod){
		super(testMethod);
	}
	
	/**
	 * Sub classes must implement the output behaviour of the test type. 
	 * @param result TestResult of the test instance
	 * @throws Exception
	 */
	public abstract void printTestFailure() throws Exception;

	public abstract String getScraperTestId();
	
	/**
	 * Class of the tested Scraper
	 * @return Scraper
	 */
	public Class getScraperClass() {
		return scraper.getClass();
	}

	/**
	 * tested Scraper
	 * @return 
	 */
	public Scraper getScraper() {
		return this.scraper;
	}
	
	/**
	 * returns true if the test is enabled in UnitTestData.xml
	 * @return
	 */
	public abstract boolean isEnabled();

}
