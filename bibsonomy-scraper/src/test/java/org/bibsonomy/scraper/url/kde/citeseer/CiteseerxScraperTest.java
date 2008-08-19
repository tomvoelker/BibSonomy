package org.bibsonomy.scraper.url.kde.citeseer;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Scraper URL tests #112 for CiteseerxScraperTest
 * @author tst
 *
 */
public class CiteseerxScraperTest {
	
	/**
	 * starts URL test with id url_112
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_112"));
	}

}
