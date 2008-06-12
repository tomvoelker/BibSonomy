package org.bibsonomy.scraper.url.kde.psycontent;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Scraper URL tests #94 & #95 for PsyContentAScraper
 * @author wbi
 * @version $Id$
 */
public class PsyContentScraperTest {
	
	/**
	 * starts URL test with id url_94
	 */
	@Test
	@Ignore
	public void urlTest1Run(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_94"));
	}
	
	/**
	 * starts URL test with id url_95
	 */
	@Test
	@Ignore
	public void urlTestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_95"));
	}

}
