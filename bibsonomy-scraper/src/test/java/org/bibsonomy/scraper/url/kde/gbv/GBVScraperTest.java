package org.bibsonomy.scraper.url.kde.gbv;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author hmi
 * @version $Id$
 */
public class GBVScraperTest {
	/**
	 * 
	 */
	@Test
	@Ignore
	public void urlTestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_232"));
	}
}
