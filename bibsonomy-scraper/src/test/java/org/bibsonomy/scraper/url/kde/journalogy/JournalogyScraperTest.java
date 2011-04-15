package org.bibsonomy.scraper.url.kde.journalogy;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Test;
import org.junit.Ignore;

/**
 * Scraper URL tests #193 & #194
 * @author clemens
 * @version $Id$
 */
public class JournalogyScraperTest {
	/**
	 * starts URL test with id url_193
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_193"));
	}
	
	/**
	 * starts URL test with id url_194
	 */
	@Test
	@Ignore
	public void url2TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_194"));
	}
	
	/**
	 * starts URL test with id url_195
	 */
	@Test
	@Ignore
	public void url3TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_195"));
	}
}
