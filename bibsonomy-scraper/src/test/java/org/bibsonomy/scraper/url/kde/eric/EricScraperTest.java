package org.bibsonomy.scraper.url.kde.eric;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Scraper URL tests #99 for EricScraper
 * @author tst
 * @version $Id$
 */
public class EricScraperTest {
	
	/**
	 * starts URL test with id url_99
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_99"));
	}

}
