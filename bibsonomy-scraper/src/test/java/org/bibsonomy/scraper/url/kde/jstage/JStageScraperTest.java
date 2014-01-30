package org.bibsonomy.scraper.url.kde.jstage;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Test;

/**
 * @author Haile
 */
public class JStageScraperTest {
	/**
	 * starts URL test with id url_196
	 */
	@Test
	public void url1TestRun(){
		assertTrue(UnitTestRunner.runSingleTest("url_196"));
	}

}
