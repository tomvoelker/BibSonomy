package org.bibsonomy.scraper.url.kde.liebert;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Scraper URL tests #88 & #89 for LiebertScraper
 * @author wbi
 * @version $Id$
 */
@Ignore
public class LiebertScraperTest {
	
	/**
	 * starts URL test with id url_88
	 */
	@Test
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_88"));
	}
	
	/**
	 * starts URL test with id url_89
	 */
	@Test
	public void url2TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_89"));
	}
}
