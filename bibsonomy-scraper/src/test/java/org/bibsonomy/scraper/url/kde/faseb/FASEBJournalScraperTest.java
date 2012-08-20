package org.bibsonomy.scraper.url.kde.faseb;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author wla
 * @version $Id$
 */
public class FASEBJournalScraperTest {

	/**
	 * starts URL test with id url_224, url_225, url_226, url_227
	 */
	@Test
	@Ignore
	public void urlTestRun() {
		final UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_224"));
		assertTrue(runner.runSingleTest("url_225"));
		assertTrue(runner.runSingleTest("url_226"));
		assertTrue(runner.runSingleTest("url_227"));
	}

}
