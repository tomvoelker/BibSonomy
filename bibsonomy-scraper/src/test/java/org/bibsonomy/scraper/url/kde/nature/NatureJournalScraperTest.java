package org.bibsonomy.scraper.url.kde.nature;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL tests for NatureJournalScraper
 *
 * @author Johannes
 */
@Category(RemoteTest.class)
public class NatureJournalScraperTest {

	/**
	 * 
	 */
	@Test
	public void urlTest1Run(){
		final String url = "http://www.nature.com/nrn/journal/v18/n1/full/nrn.2016.150.html";
		final String resultFile = "NatureJournalScraperUnitURLTest1.bib";
		assertScraperResult(url, null, NatureJournalScraper.class, resultFile);
	}
	
	/**
	 * 
	 */
	@Test
	public void urlTest2Run(){
		final String url = "http://www.nature.com/onc/journal/v34/n38/full/onc2014416a.html";
		final String resultFile = "NatureJournalScraperUnitURLTest2.bib";
		assertScraperResult(url, null, NatureJournalScraper.class, resultFile);
	}
}
