package scraper.url.kde.plos;

import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import scraper.UnitTestRunner;

/**
 * Scraper URL tests #43 & #44 for PlosScraper
 * @author tst
 */
public class PlosScraperTest {
	
	/**
	 * starts URL test with id url_43
	 */
	@Test
	@Ignore
	public void urlTest1Run(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_43"));
	}
	
	/**
	 * starts URL test with id url_44
	 */
	@Test
	@Ignore
	public void urlTest2Run(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_44"));
	}

}
