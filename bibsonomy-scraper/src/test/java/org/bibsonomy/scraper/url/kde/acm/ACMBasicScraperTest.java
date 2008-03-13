package org.bibsonomy.scraper.url.kde.acm;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Scraper URL tests #1 for ACMBasicSCraper  
 * @author tst
 *
 */
public class ACMBasicScraperTest {
	
	/**
	 * starts URL test with id url_1
	 */
	@Test
	@Ignore
	public void urlTestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_1"));
	}
	
}
