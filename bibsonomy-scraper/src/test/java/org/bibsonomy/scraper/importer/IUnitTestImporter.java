package org.bibsonomy.scraper.importer;

import java.util.List;

import org.bibsonomy.scraper.ScraperUnitTest;


/**
 * interface which descripes all needed methods for Importer 
 * @author tst
 *
 */
public interface IUnitTestImporter {
	
	/**
	 * Reads tests from external source and generate a List which contains
	 * ScraperUnitTests.
	 * @return List with ScraperUnitTests
	 * @throws Exception
	 */
	public List<ScraperUnitTest> getUnitTests() throws Exception;

}
