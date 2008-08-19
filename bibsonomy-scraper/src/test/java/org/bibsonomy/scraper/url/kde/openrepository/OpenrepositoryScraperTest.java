package org.bibsonomy.scraper.url.kde.openrepository;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Scraper URL tests #117. #118, #199, #120 for OpenrepositoryScraper
 * @author tst
 * @version $Id$
 */
public class OpenrepositoryScraperTest {
	
	/**
	 * starts URL test with id url_117
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_117"));
	}
	
	/**
	 * starts URL test with id url_118
	 */
	@Test
	@Ignore
	public void url2TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_118"));
	}
	
	/**
	 * starts URL test with id url_119
	 */
	@Test
	@Ignore
	public void url3TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_119"));
	}
	
	/**
	 * starts URL test with id url_120
	 */
	@Test
	@Ignore
	public void url4TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_120"));
	}

}
