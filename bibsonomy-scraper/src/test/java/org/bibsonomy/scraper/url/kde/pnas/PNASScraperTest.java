package org.bibsonomy.scraper.url.kde.pnas;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Scraper URL test #191 #192 for PNASScraper
 * 
 * @author clemens
 * @version $Id$
 */
public class PNASScraperTest {

	/**
	 * starts URL test with id url_191
	 */
	@Test
	@Ignore
	public void urlTest1Run() {
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_191"));
	}

	/**
	 * starts URL test with id url_192
	 */
	@Test
	@Ignore
	public void urlTest2Run() {
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_192"));
	}
}
