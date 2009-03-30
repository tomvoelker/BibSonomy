package org.bibsonomy.scraper.url.kde.agu;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * URL Tests for AGUScraper #
 * @author tst
 * @version $Id$
 */
public class AGUScraperTest {

	/**
	 * starts URL test with id url_146
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_146"));
	}
	
}
