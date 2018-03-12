package org.bibsonomy.scraper.url.kde.base;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.junit.Test;

/**
 * TODO: add documentation to this class
 *
 * @author rja
 */
public class BaseScraperTest {

	/**
	 * Test for URL 1
	 */
	@Test
	public void testUrl1() {
		final String url = "https://www.base-search.net/Record/a3643e97bbaec922cff6ff853dff6bb1f442ac60c14352064504b2a58c2eb24f/";
		assertScraperResult(url, null, BaseScraper.class, "BaseScraperUnitURLTest1.bib");
	}

	/**
	 * Test for URL 2
	 */
	@Test
	public void testUrl2() {
		final String url = "https://www.base-search.net/Record/a68b394bd5b038827fb2c3e4c9b0ebf9a818bcbb61eaacee1159d116a7047224//";
		assertScraperResult(url, null, BaseScraper.class, "BaseScraperUnitURLTest2.bib");
	}

}
