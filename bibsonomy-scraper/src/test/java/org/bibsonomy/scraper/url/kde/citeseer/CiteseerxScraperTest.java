package org.bibsonomy.scraper.url.kde.citeseer;

import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.Assert;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Scraper URL tests #112 for CiteseerxScraperTest
 * @author tst
 *
 */
public class CiteseerxScraperTest {
	
	/**
	 * starts URL test with id url_112
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_112"));
	}
	
	@Ignore
	@Test
	public void test1() throws MalformedURLException {
		final String url = "http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.14.7185";
		final ScrapingContext sc = new ScrapingContext(new URL(url));
		
		final CiteseerxScraper scraper = new CiteseerxScraper();
		
		try {
			final boolean scrape = scraper.scrape(sc);
			Assert.assertTrue(scrape);
		} catch (ScrapingException ex) {
			Assert.fail(ex.getMessage());
		}
		
		
	}

}
