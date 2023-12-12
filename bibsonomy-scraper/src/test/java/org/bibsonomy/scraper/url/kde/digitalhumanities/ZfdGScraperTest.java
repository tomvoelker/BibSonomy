package org.bibsonomy.scraper.url.kde.digitalhumanities;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import org.bibsonomy.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(RemoteTest.class)
public class ZfdGScraperTest {
	String resultDirectory = "digitalhumanities/zfdg/";

	@Test
	public void urlTest1Run(){
		final String url = "https://zfdg.de/sb005_008";
		final String resultFile = resultDirectory + "ZfdGScraperUnitURLTest1.bib";
		assertScraperResult(url, ZfdGScraper.class, resultFile);
	}
}
