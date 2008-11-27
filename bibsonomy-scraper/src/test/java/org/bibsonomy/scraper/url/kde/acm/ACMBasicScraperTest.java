package org.bibsonomy.scraper.url.kde.acm;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Scraper URL tests #1 #134 for ACMBasicSCraper  
 * @author tst
 *
 */
public class ACMBasicScraperTest {
	
	/**
	 * starts URL test with id url_1
	 */
	@Test
	@Ignore
	public void urlTestRun1(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_1"));
	}

	/**
	 * starts URL test with id url_134
	 */
	@Test
	@Ignore
	public void urlTestRun2(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_134"));
	}
	
}
