package org.bibsonomy.scraper.url.kde.arxiv;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Scraper URL tests #10 #126 #129 for ArxivScraper
 * @author tst
 *
 */
public class ArxivScraperTest {
	
	/**
	 * starts URL test with id url_10
	 */
	@Test
	@Ignore
	public void urlTestRun1(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_10"));
	}
	
	/**
	 * starts URL test with id url_126
	 */
	@Test
	@Ignore
	public void urlTestRun2(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_126"));
	}
	
	/**
	 * starts URL test with id url_129
	 */
	@Test
	@Ignore
	public void urlTestRun3(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_129"));
	}
	
}
