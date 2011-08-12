package org.bibsonomy.scraper.url.kde.inspire;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Test;

/**
 * Scraper URL tests #196
 * @author clemens
 * @version $Id$
 */
public class InspireScraperTest {
	/**
	 * starts URL test with id url_196
	 */
	@Test
	//@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_196"));
	}

}
