package scraper;

import java.net.URL;
import java.util.List;

import junit.framework.TestResult;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Ignore;
import org.junit.Test;

import scraper.importer.IUnitTestImporter;
import scraper.importer.xml.XMLUnitTestImporter;

/**
 * Main class which starts the scraper unit test.
 * @author tst
 *
 */
public class UnitTestRunner {
	
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
		URL log4j = new UnitTestRunner().getClass().getResource("log4j.properties");
		PropertyConfigurator.configure(log4j);
		try {
			if(importer == null)
				throw new Exception("no UnitTestImporter available");
			
			List<ScraperUnitTest> unitTests = importer.getUnitTests();
			
			for(ScraperUnitTest test : unitTests){
				TestResult result = test.run();
				test.printTestFailure(result);
			}
			
		} catch (Exception e) {
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
					if(result.errorCount() > 0 || result.failureCount() > 0)
						return false;
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
	 * starts the whole party
	 * @param args not needed
	 */
	public static void main(String[] args){
		new UnitTestRunner().run();
	}

}
