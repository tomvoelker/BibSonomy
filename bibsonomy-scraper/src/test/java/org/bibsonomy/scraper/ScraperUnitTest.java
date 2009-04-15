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

}
