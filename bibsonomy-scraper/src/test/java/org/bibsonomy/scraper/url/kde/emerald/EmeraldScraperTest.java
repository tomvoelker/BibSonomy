package org.bibsonomy.scraper.url.kde.emerald;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * TODO: add documentation to this class
 *
 * @author rja
 */
@Category(RemoteTest.class)
public class EmeraldScraperTest {

	/**
	 * starts URL test with id url_230 for the host
	 * http://www.emeraldinsight.com/
	 */
	@Test
	public void url4TestRun() {
		final String url = "http://www.emeraldinsight.com/doi/abs/10.1108/S1876-0562%282004%290000004009";
		final String resultFile = "APHAScraperUnitURLTest4.bib";
		assertScraperResult(url, null, EmeraldScraper.class, resultFile);
	}

}
