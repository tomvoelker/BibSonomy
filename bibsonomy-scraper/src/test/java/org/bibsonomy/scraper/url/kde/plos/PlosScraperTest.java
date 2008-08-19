package org.bibsonomy.scraper.url.kde.plos;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Scraper URL tests #43 & #44 for PlosScraper
 * 
 * TODO:
 * This test works only on Java in 64bit version. The problem is a regex
 * in the endote converter which thorws a StackOverFlowException, because of
 * the huge abstracts (it seems that any citation on plos.org has large abstracts).
 * 
 * @author tst
 */
public class PlosScraperTest {
	
	/**
	 * starts URL test with id url_43
	 */
	@Test
	@Ignore
	public void urlTest1Run(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_43"));
	}
	
	/**
	 * starts URL test with id url_44
	 */
	@Test
	@Ignore
	public void urlTest2Run(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_44"));
	}

}
