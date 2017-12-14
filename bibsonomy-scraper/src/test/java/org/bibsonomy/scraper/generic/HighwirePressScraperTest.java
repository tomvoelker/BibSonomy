package org.bibsonomy.scraper.generic;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * tests for {@link HighwirePressScraper}
 *
 * @author Johannes
 */
@Category(RemoteTest.class)
public class HighwirePressScraperTest {

	@Test
	public void testHighwirePressScraper1() {
		final String url = "https://www.biorxiv.org/content/early/2017/10/06/199430";
		final String resultFile = "HighwirePressScraperTest1.bib";
		assertScraperResult(url, null, HighwirePressScraper.class, resultFile);
	}
	
	@Test
	public void testHighwirePressScraper2() {
		final String url = "http://onlinelibrary.wiley.com/doi/10.1002/scj.20874/full";
		final String resultFile = "HighwirePressScraperTest2.bib";
		assertScraperResult(url, null, HighwirePressScraper.class, resultFile);
	}
	
	@Test
	public void testHighwirePressScraper3() {
		final String url = "https://www.osapublishing.org/jlt/abstract.cfm?uri=jlt-35-20-4553";
		final String resultFile = "HighwirePressScraperTest3.bib";
		assertScraperResult(url, null, HighwirePressScraper.class, resultFile);
	}
	
	@Test
	public void testSupportsScrapingContext() throws MalformedURLException {
		ScrapingContext scrapingContext = new ScrapingContext(new URL("https://www.biorxiv.org/content/early/2017/10/06/199430"));
		assertTrue(new HighwirePressScraper().supportsScrapingContext(scrapingContext));
	}
}
