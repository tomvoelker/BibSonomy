package org.bibsonomy.scraper.url.kde.sciencemag;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Scraper URL test #192 for ScienceMagScraper
 * 
 * @author clemens
 * @version $Id$
 */
public class ScienceMagScraperTest {

	/**
	 * starts URL test with id url_192
	 */
	@Test
	@Ignore
	public void urlTest1Run() {
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_192"));
	}
}
