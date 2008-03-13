package org.bibsonomy.scraper.url.kde.librarything;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Scraper URL tests #18 #19 #37 for LibrarythingScraper
 * @author tst
 *
 */
public class LibrarythingScraperTest {
	
	/**
	 * starts URL test with id url_18
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_18"));
	}

	/**
	 * starts URL test with id url_19
	 */
	@Test
	@Ignore
	public void url2TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_19"));
	}

	/**
	 * starts URL test with id url_37
	 */
	@Test
	@Ignore
	public void url3TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_37"));
	}

}
