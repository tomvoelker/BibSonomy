package org.bibsonomy.scraper.url.kde.muse;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Scraper URL tests #113 , #114 for ProjectmuseScraper
 * @author tst
 */
public class ProjectmuseScraperTest {

	/**
	 * starts URL test with id url_113
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_113"));
	}

	/**
	 * starts URL test with id url_114
	 */
	@Test
	@Ignore
	public void url2TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_114"));
	}

}
