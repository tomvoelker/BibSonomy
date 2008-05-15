package org.bibsonomy.scraper.url.kde.editlib;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Scraper URL tests #70 & #71 for EditLibScraper
 * @author wbi
 * @version $Id$
 */
public class EditLibScraperTest {
	/**
	 * starts URL test with id url_70
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_70"));
	}
	
	/**
	 * starts URL test with id url_71
	 */
	@Test
	@Ignore
	public void url2TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_71"));
	}
}
