package org.bibsonomy.scraper.url.kde.bmj;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Scraper URL tests #58 & #59 for DBLPScraper
 * @author wbi
 * @version $Id$
 */
public class BMJScraperTest {
	/**
	 * starts URL test with id url_68
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_68"));
	}
	
	/**
	 * starts URL test with id url_69
	 */
	@Test
	@Ignore
	public void url2TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_69"));
	}
}
