package org.bibsonomy.scraper.url.kde.blackwell;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Scraper URL tests #51 #52 for BlackwellSynergyScraper
 * @author tst
 *
 */
public class BlackwellSynergyScraperTest {

	/**
	 * starts URL test with id url_51
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_51"));
	}

	/**
	 * starts URL test with id url_52
	 */
	@Test
	@Ignore
	public void url2TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_52"));
	}

	/**
	 * starts URL test with id url_53
	 */
	@Test
	@Ignore
	public void url3TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_53"));
	}

}