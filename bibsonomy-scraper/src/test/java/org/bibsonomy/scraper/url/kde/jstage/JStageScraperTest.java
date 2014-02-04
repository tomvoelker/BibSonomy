package org.bibsonomy.scraper.url.kde.jstage;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * @author Haile
 */
@Category(RemoteTest.class)
public class JStageScraperTest {
	/**
	 * starts URL test with id url_267
	 */
	@Test
	public void url1TestRun(){
		assertTrue(UnitTestRunner.runSingleTest("url_267"));
	}

}
