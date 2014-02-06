package org.bibsonomy.scraper.url.kde.degruyter;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests url_268
 * @author Haile
 */
@Category(RemoteTest.class)
public class DeGruyterScraperTest {
	/**
	 * starts URL test with id url_268
	 */
	@Test
	public void urlTest1Run(){
		assertTrue(UnitTestRunner.runSingleTest("url_268"));
	}
}
