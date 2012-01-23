package org.bibsonomy.scraper.url.kde.ats;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author clemens
 * @version $Id$
 */
public class ATSScraperTest {

	/**
	 * starts URL test with id url_212
	 */
	@Test
	@Ignore
	public void urlTest1Run() {
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_212"));
	}
	/**
	 * starts URL test with id url_213
	 */
	@Test
	@Ignore
	public void urlTest2Run() {
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_213"));
	}
	/**
	 * starts URL test with id url_214
	 */
	@Test
	@Ignore
	public void urlTest3Run() {
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_214"));
	}
}
