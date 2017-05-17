package org.bibsonomy.scraper.url.kde.rspb;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests for {@link RSPBScraper}
 *
 * @author Johannes
 */
@Category(RemoteTest.class)
public class RSPBScraperTest {

	@Test
	public void url1TestRun(){
		final String url = "http://rspb.royalsocietypublishing.org/content/283/1844/20161270";
		final String resultFile = "RSPBScraperUnitURLTest1.bib";
		assertScraperResult(url, null, RSPBScraper.class, resultFile);
	}
	
	@Test
	public void url2TestRun(){
		final String url = "http://rspb.royalsocietypublishing.org/content/283/1838/20160847";
		final String resultFile = "RSPBScraperUnitURLTest2.bib";
		assertScraperResult(url, null, RSPBScraper.class, resultFile);
	}
}
