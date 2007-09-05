package scraper.test;

import junit.framework.TestCase;
import junit.framework.TestResult;

/**
 * Basic data structure for scraper unit tests.
 * All test types must derive this class.
 * @author tst
 *
 */
public abstract class ScraperUnitTest extends TestCase{
	
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
	public abstract void printTestFailure(TestResult result) throws Exception;

}
