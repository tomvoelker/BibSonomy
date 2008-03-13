package org.bibsonomy.scraper.url.kde.dlib;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Scraper URL tests #54 & #55 for DLibScraper
 * @author tst
 */
public class DLibScraperTest {

	/**
	 * starts URL test with id url_54
	 */
	@Test
	@Ignore
	public void urlTest1Run(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_54"));
	}
		
	/**
	 * starts URL test with id url_55
	 */
	@Test
	@Ignore
	public void urlTest2Run(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_55"));
	}

}
