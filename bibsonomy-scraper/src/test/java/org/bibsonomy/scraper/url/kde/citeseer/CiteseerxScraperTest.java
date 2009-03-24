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
	
	@Test
	@Ignore
	public void test1() throws MalformedURLException {
		final String url = "http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.14.7185";
		final ScrapingContext sc = new ScrapingContext(new URL(url));
		
		final CiteseerxScraper scraper = new CiteseerxScraper();
		
		try {
			final boolean scrape = scraper.scrape(sc);
			System.out.println(sc.getBibtexResult());
			Assert.assertTrue(scrape);
		} catch (ScrapingException ex) {
			Assert.fail(ex.getMessage());
		}
		
		
	}

	@Test
	@Ignore
	public void runTest1() throws MalformedURLException {
		String url = "http://citeseerx.ist.psu.edu/viewdoc/summary;jsessionid=352C9BD0F67928E2EDAFA8B58ACFBFB9?doi=10.1.1.110.903";
		final CiteseerxScraper scraper = new CiteseerxScraper();
		final ScrapingContext sc = new ScrapingContext(new URL(url));
		
		try {
			scraper.scrape(sc);
			System.out.println(sc.getBibtexResult());
		} catch (ScrapingException ex) {
			Assert.fail(ex.getMessage());
		}
	}
}
