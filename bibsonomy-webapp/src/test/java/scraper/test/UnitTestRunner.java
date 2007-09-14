package scraper.test;

import java.net.URL;
import java.util.List;

import junit.framework.TestResult;

import static org.junit.Assert.assertTrue;

import org.apache.log4j.PropertyConfigurator;

import scraper.test.importer.IUnitTestImporter;
import scraper.test.importer.xml.XMLUnitTestImporter;

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
	private void run(){
		URL log4j = new UnitTestRunner().getClass().getResource("log4j.properties");
		PropertyConfigurator.configure(log4j);
		try {
			if(importer == null)
				throw new Exception("no UnitTestImporter available");
			
			List<ScraperUnitTest> unitTests = importer.getUnitTests();
			
			for(ScraperUnitTest test : unitTests){
				//TestResult result = test.run();
				//test.printTestFailure(result);
			}
			
		} catch (Exception e) {
			ParseFailureMessage.printParseFailureMessage(e, "main class");
		}
	}
	
	/**
	 * starts the whole party
	 * @param args not needed
	 */
	public static void main(String[] args){
		new UnitTestRunner().run();
	}

}
