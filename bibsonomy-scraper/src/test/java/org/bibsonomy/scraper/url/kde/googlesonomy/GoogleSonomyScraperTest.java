package org.bibsonomy.scraper.url.kde.googlesonomy;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Scraper URL tests #138 for GoogleSonomyScraper
 * @author tst
 * @version $Id$
 */
public class GoogleSonomyScraperTest {

	/**
	 * starts URL test with id url_138
	 */
	@Test
	@Ignore
	public void urlTestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_138"));
	}
}
