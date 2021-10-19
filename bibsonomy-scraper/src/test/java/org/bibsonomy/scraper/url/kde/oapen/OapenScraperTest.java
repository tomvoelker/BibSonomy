package org.bibsonomy.scraper.url.kde.oapen;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.bibsonomy.scraper.url.kde.nrc.NRCScraper;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(RemoteTest.class)
public class OapenScraperTest {

	@Test
	public void url1TestRun() {
		final String url = "https://library.oapen.org/handle/20.500.12657/31846";
		final String resultFile = "OapenScraperUnitURLTest1.bib";
		assertScraperResult(url, null, OapenScraper.class, resultFile);

	}

	@Test
	public void url2TestRun() {
		final String url = "https://library.oapen.org/handle/20.500.12657/37140";
		final String resultFile = "OapenScraperUnitURLTest2.bib";
		assertScraperResult(url, null, OapenScraper.class, resultFile);

	}

	@Test
	public void url3TestRun() {
		final String url = "https://library.oapen.org/handle/20.500.12657/46536";
		final String resultFile = "OapenScraperUnitURLTest3.bib";
		assertScraperResult(url, null, OapenScraper.class, resultFile);

	}
}
