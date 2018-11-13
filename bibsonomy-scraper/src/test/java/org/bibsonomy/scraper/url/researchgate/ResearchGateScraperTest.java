package org.bibsonomy.scraper.url.researchgate;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(RemoteTest.class)
public class ResearchGateScraperTest {

	@Test
	public void testReserchGate() {
		assertScraperResult("https://www.researchgate.net/publication/266654512_An_analysis_of_tag-recommender_evaluation_procedures", null, ResearchGateScraper.class, "researchgate/ResearchGateTest1.bib");
	}
}