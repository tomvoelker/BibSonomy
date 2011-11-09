package org.bibsonomy.scraper.url.kde.googlebooks;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Scraper URL tests #207, #208 for GoogleBooksScraper
 * @author clemens
 */
public class GoogleBooksScraperTest {

	/**
	 * starts URL test with id url_207
	 */
	@Test
	@Ignore
	public void urlTestRun1(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_207"));
	}
	
	/**
	 * starts URL test with id url_208
	 */
	@Test
	@Ignore
	public void urlTestRun2(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_208"));
	}
}
