package org.bibsonomy.scraper.url.kde.sage;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author wla
 * @version $Id$
 */
public class SageCDPScraperTest {
	/**
	 * starts URL test with id url_242
	 */
	@Test
	@Ignore
	public void url1TestRun() {
		final UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_242"));
	}
}
