package org.bibsonomy.scraper.url.kde.jstor;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Scraper URL tests #97 & #98 for JStorScraper
 * @author wbi
 * @version $Id$
 */
@Ignore
public class JStorScraperTest {
	
	/**
	 * starts URL test with id url_97
	 */
	@Test
	public void urlTest1Run(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_97"));
	}
	
	/**
	 * starts URL test with id url_98
	 */
	@Test
	public void urlTest2Run(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_98"));
	}
}
