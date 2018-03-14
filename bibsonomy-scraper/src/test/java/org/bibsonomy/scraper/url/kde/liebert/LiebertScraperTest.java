package org.bibsonomy.scraper.url.kde.liebert;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.junit.Test;

/**
 * TODO: add documentation to this class
 *
 * @author rja
 */
public class LiebertScraperTest {

	/**
	 * starts URL test with id url_88
	 */
	@Test
	public void url1TestRun(){
		final String url = "http://www.liebertonline.com/doi/abs/10.1089/152308604773934350";
		final String resultFile = "LiebertScraperUnitURLTest1.bib";
		assertScraperResult(url, null, LiebertScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_89
	 */
	@Test
	public void url2TestRun(){
		final String url = "http://www.liebertonline.com/action/showCitFormats?doi=10.1089%2F152308604773934350";
		final String resultFile = "LiebertScraperUnitURLTest3.bib";
		assertScraperResult(url, null, LiebertScraper.class, resultFile);
	}
	
	/**
	 * starts URL test with id url_248
	 */
	@Test
	public void url3TestRun(){
		final String url = "http://online.liebertpub.com/doi/abs/10.1089/teb.2007.0150";
		final String resultFile = "LiebertScraperUnitURLTest2.bib";
		assertScraperResult(url, null, LiebertScraper.class, resultFile);
	}

}
