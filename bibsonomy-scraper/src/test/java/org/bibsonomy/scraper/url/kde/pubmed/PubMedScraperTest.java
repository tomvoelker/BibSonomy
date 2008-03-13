package org.bibsonomy.scraper.url.kde.pubmed;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Scraper URL tests #27 #35 for PubMedScraper
 * @author tst
 *
 */
public class PubMedScraperTest {
	
	/**
	 * starts URL test with id url_27
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_27"));
	}
	
	/**
	 * starts URL test with id url_35
	 */
	@Test
	@Ignore
	public void url2TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_35"));
	}

}