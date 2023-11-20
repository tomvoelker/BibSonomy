package org.bibsonomy.scraper.url.kde.copernicus;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import org.bibsonomy.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(RemoteTest.class)
public class CopernicusPreprintScraperTest {
	String resultDirectory = "copernicus/preprints/";

	@Test
	public void url1TestRun(){
		final String url = "https://bg.copernicus.org/preprints/bg-2021-311/";
		final String resultFile = resultDirectory + "CopernicusPreprintScraperUnitURLTest1.bib";
		assertScraperResult(url, null, CopernicusPreprintScraper.class, resultFile);
	}

	@Test
	public void url2TestRun(){
		final String url = "https://acp.copernicus.org/preprints/acp-2021-763/";
		final String resultFile = resultDirectory + "CopernicusPreprintScraperUnitURLTest2.bib";
		assertScraperResult(url, null, CopernicusPreprintScraper.class, resultFile);
	}

	@Test
	public void url3TestRun(){
		final String url = "https://angeo.copernicus.org/preprints/angeo-2022-3/";
		final String resultFile = resultDirectory + "CopernicusPreprintScraperUnitURLTest3.bib";
		assertScraperResult(url, null, CopernicusPreprintScraper.class, resultFile);
	}

	@Test
	public void url4TestRun(){
		final String url = "https://amt.copernicus.org/preprints/amt-2022-13/";
		final String resultFile = resultDirectory + "CopernicusPreprintScraperUnitURLTest4.bib";
		assertScraperResult(url, null, CopernicusPreprintScraper.class, resultFile);
	}
}
