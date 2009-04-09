package org.bibsonomy.scraper.generic;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Scraper url tests #147, #148, #149, #150 for EprintScraper
 * 
 * @author tst
 * @version $Id$
 */
public class EprintScraperTest {
	
	/**
	 * starts URL test with id url_147
	 */
	@Test
	@Ignore
	public void url1TestRun1(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_147"));
	}

	/**
	 * starts URL test with id url_148
	 */
	@Test
	@Ignore
	public void url1TestRun2(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_148"));
	}
	
	/**
	 * starts URL test with id url_149
	 */
	@Test
	@Ignore
	public void url1TestRun3(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_149"));
	}
	
	/**
	 * starts URL test with id url_150
	 */
	@Test
	@Ignore
	public void url1TestRun4(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_150"));
	}
}
