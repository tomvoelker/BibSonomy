package org.bibsonomy.scraper.url.kde.igiglobal;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Test;

/**
 * @author Haile
 * @version $Id$
 */
public class IGIGlobalScraperTest {
	/**
	 * starts URL test with id url_265
	 */
	@Test
	public void urlTestRun() {
		assertTrue(UnitTestRunner.runSingleTest("url_265"));
	}
}
