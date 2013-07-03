package org.bibsonomy.scraper.generic;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Lukas
 * @version $Id$
 */
public class DublinCoreScraperTest {

	@Ignore
	@Test
	public void testDCScraper() {
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_260"));
	}
	
}
