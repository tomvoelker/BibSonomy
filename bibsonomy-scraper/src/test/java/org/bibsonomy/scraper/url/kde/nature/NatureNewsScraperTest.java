package org.bibsonomy.scraper.url.kde.nature;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests for NatureNewsScraper
 *
 * @author Johannes
 */
@Category(RemoteTest.class)
public class NatureNewsScraperTest {

	@Test
	public void urlTest1Run() {
		final String url = "http://www.nature.com/news/online-collaboration-scientists-and-the-social-network-1.15711#/correction1";
		final String resultFile = "NatureNewsScraperUnitURLTest1.bib";
		assertScraperResult(url, null, NatureNewsScraper.class, resultFile);		
	}
	
	@Test
	public void urlTest2Run() {
		final String url = "http://www.nature.com/news/how-facebook-fake-news-and-friends-are-warping-your-memory-1.21596";
		final String resultFile = "NatureNewsScraperUnitURLTest2.bib";
		assertScraperResult(url, null, NatureNewsScraper.class, resultFile);		
	}
}
