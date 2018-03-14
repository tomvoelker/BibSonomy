package org.bibsonomy.scraper.url.kde.hogrefe;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.junit.Test;

/**
 * TODO: add documentation to this class
 *
 * @author rja
 */
public class HogrefeScraperTest {

	/**
	 * new URL
	 */
	@Test
	public void url4TestRun(){
		final String url = "https://econtent.hogrefe.com/doi/abs/10.1027/1864-9335/a000179";
		final String resultFile = "LiteratumScraperUnitURLTest1.bib";
		assertScraperResult(url, null, HogrefeScraper.class, resultFile);
	}

}
