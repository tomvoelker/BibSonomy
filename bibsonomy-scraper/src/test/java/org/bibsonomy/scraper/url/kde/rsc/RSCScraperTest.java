package org.bibsonomy.scraper.url.kde.rsc;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author wla
 * @version $Id$
 */
public class RSCScraperTest {

	/**
	 * starts URL test with id url_223
	 */
	@Test
	@Ignore
	public void urlTestRun() {
		final UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_223"));
	}

}
