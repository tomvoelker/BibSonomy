package org.bibsonomy.scraper.url.kde.uiuc;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Scraper URL tests #86 & #87 for ISRLScraper
 * @author wbi
 * @version $Id$
 */
public class ISRLScraperTest {
	
	/**
	 * starts URL test with id url_86
	 */
	@Test
	@Ignore
	public void urlTestRun1(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_86"));
	}
	
	/**
	 * starts URL test with id url_87
	 */
	@Test
	@Ignore
	public void urlTestRun2(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_87"));
	}
}
