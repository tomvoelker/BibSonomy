package org.bibsonomy.scraper.url.kde.aps;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Haile
 * @version $Id$
 */
public class ApsScraperTest {

	@SuppressWarnings("javadoc")
	@Test
	@Ignore
	public void urlTestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_255"));
	}
	
}
