package org.bibsonomy.scraper.url.kde.morganclaypool;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests #111 for MetapressScraper
 * @author tst
 *
 */
@Category(RemoteTest.class)
public class MorganClaypoolScraperTest {

	/**
	 * starts URL test with id url_273
	 */
	@Test
	public void url1TestRun(){
		assertTrue(UnitTestRunner.runSingleTest("url_273"));
	}
	/**
	 * starts URL test with id url_274
	 */
	@Test
	public void url2TestRun(){
		assertTrue(UnitTestRunner.runSingleTest("url_274"));
	}
}
