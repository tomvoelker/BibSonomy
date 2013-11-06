package org.bibsonomy.scraper.url.kde.bloodjournal;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * @author Haile
 * @version $Id$
 */
@Category(RemoteTest.class)
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
