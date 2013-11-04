package org.bibsonomy.scraper.url.kde.bloodjournal;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Test;

/**
 * @author Haile
 * @version $Id$
 */
public class BloodJournalScraperTest {
	/**
	 * @param args
	 */
	/**
	 * starts URL test with id url_264
	 */
	@Test
	public void urlTestRun() {
		assertTrue(UnitTestRunner.runSingleTest("url_264"));
	}
}
