package org.bibsonomy.scraper.url.openresearch;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.bibsonomy.scraper.url.openreview.OpenReviewScraper;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(RemoteTest.class)
public class OpenReviewScraperTest {

	@Test
	public void testReserchGate() {
		assertScraperResult("https://openreview.net/forum?id=nzpLWnVAyah", null, OpenReviewScraper.class, "openreview/OpenReviewTest1.bib");

		assertScraperResult("https://openreview.net/pdf?id=nzpLWnVAyah", null, OpenReviewScraper.class, "openreview/OpenReviewTest1.bib");
	}

}
