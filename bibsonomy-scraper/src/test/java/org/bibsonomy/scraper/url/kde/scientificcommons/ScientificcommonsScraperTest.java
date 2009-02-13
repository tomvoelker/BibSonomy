package org.bibsonomy.scraper.url.kde.scientificcommons;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Scraper URL tests #139 #140 for ScientificcommonsScraper 
 * @author tst
 * @version $Id$
 */
public class ScientificcommonsScraperTest {
	
	/**
	 * starts URL test with id url_139
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_139"));
	}
	
	/**
	 * starts URL test with id url_140
	 */
	@Test
	@Ignore
	public void url2TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_140"));
	}

}
