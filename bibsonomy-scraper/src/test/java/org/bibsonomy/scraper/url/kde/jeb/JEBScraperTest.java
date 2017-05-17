package org.bibsonomy.scraper.url.kde.jeb;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests for {@link JEBScraper}
 *
 * @author Johannes
 */
@Category(RemoteTest.class)
public class JEBScraperTest {

	@Test
	public void url1TestRun(){
		final String url = "http://jeb.biologists.org/content/219/19/3137";
		final String resultFile = "JEBScraperUnitURLTest1.bib";
		assertScraperResult(url, null, JEBScraper.class, resultFile);
	}
	
	@Test
	public void url2TestRun(){
		final String url = "http://jeb.biologists.org/content/early/2017/04/05/jeb.140509";
		final String resultFile = "JEBScraperUnitURLTest2.bib";
		assertScraperResult(url, null, JEBScraper.class, resultFile);
	}
}
