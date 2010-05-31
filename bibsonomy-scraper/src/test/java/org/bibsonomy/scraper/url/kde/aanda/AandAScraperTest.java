package org.bibsonomy.scraper.url.kde.aanda;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author DaiLL
 * @version $Id$
 */
public class AandAScraperTest {
	/**
	 * starts URL test with id url_181
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_181"));
	}

	/**
	 * starts URL test with id url_182
	 */
	@Test
	@Ignore
	public void url2TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_182"));
	}
}
