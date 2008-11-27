package org.bibsonomy.scraper.url.kde.informaworld;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Scraper URL tests #77 #78 #123 #135 for DBLPScraper
 * @author wbi
 * @version $Id$
 */
public class InformaWorldScraperTest {
	
	/**
	 * starts URL test with id url_77
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_77"));
	}
	
	/**
	 * starts URL test with id url_78
	 */
	@Test
	@Ignore
	public void url2TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_78"));
	}
	
	/**
	 * starts URL test with id url_123
	 */
	@Test
	@Ignore
	public void url3TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_123"));
	}
	
	/**
	 * starts URL test with id url_135
	 */
	@Test
	@Ignore
	public void url4TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_135"));
	}
	
}
