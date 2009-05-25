package org.bibsonomy.scraper.url.kde.acm;

import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.Assert;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Scraper URL tests #1 #134 #153 for ACMBasicSCraper  
 * @author tst
 *
 */
public class ACMBasicScraperTest {
	
	/**
	 * starts URL test with id url_1
	 */
	@Test
	@Ignore
	public void urlTestRun1(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_1"));
	}

	/**
	 * starts URL test with id url_134
	 */
	@Test
	@Ignore
	public void urlTestRun2(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_134"));
	}
	
	/**
	 * starts URL test with id url_153
	 */
	@Test
	@Ignore
	public void urlTestRun3(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_153"));
	}
	
	@Test
	@Ignore
	public void test2() throws MalformedURLException {
		
		String url = "http://portal.acm.org/citation.cfm?id=500737.500755"; // abstract works
		url = "http://portal.acm.org/citation.cfm?id=1364171"; // abstract missing
		final ACMBasicScraper acm = new ACMBasicScraper();
		final ScrapingContext sc = new ScrapingContext(new URL(url));
		
		try {
			acm.scrape(sc);
		} catch (ScrapingException ex) {
			Assert.fail(ex.getMessage());
		}
	}
}
