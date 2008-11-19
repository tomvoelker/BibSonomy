package org.bibsonomy.scraper;

import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.bibsonomy.scraper.URLTest.URLScraperUnitTest;
import org.bibsonomy.scraper.importer.IUnitTestImporter;
import org.bibsonomy.scraper.importer.xml.XMLUnitTestImporter;

/**
 * Runner for reachability test for Scraper
 * @author tst
 * @version $Id$
 */
public class ReachabilityTestRunner {
	
	private static final Logger log = Logger.getLogger(ReachabilityTestRunner.class);
	
	/**
	 * Importer which reads the tests from a external sources.
	 */
	private IUnitTestImporter importer = null;
	
	/**
	 * Init Importer
	 */
	public ReachabilityTestRunner(){
		// importer for xml + bib file sources
		importer = new XMLUnitTestImporter();
	}
	
	/**
	 * This Method reads and runs the test.
	 */
	public void run(){
		URL log4j = new UnitTestRunner().getClass().getResource("log4j.properties");
		PropertyConfigurator.configure(log4j);
		try {
			if(importer == null)
				throw new Exception("no UnitTestImporter available");
			
			List<ScraperUnitTest> unitTests = importer.getUnitTests();
			
			for(ScraperUnitTest test : unitTests){
				URLScraperUnitTest urlTest = (URLScraperUnitTest) test;
				
				UrlScraper scraper = (UrlScraper) urlTest.getScraper();
				if(!scraper.supportsUrl(new URL(urlTest.getURL()))){
					log.error("In Scraper "
							+ urlTest.getScraperClass().getName()
							+ " the URL "
							+ urlTest.getURL()
							+ " is not supported.");
				}
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
		new ReachabilityTestRunner().run();
	}

}
