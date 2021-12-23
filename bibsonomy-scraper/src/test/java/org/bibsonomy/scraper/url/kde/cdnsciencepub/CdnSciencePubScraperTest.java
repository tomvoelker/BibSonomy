package org.bibsonomy.scraper.url.kde.cdnsciencepub;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(RemoteTest.class)
public class CdnSciencePubScraperTest {
	String resultDirectory = "cdnsciencepub/";

	@Test
	public void url1TestRun() {
		final String url = "https://cdnsciencepub.com/doi/full/10.1139/apnm-2020-1075";
		final String resultFile = resultDirectory + "CdnSciencePubScraperUnitURLTest1.bib";
		assertScraperResult(url, null, CdnSciencePubScraper.class, resultFile);
	}

	@Test
	public void url2TestRun() {
		final String url = "https://cdnsciencepub.com/doi/full/10.1139/anc-2020-0005";
		final String resultFile = resultDirectory + "CdnSciencePubScraperUnitURLTest2.bib";
		assertScraperResult(url, null, CdnSciencePubScraper.class, resultFile);
	}

	@Test
	public void url3TestRun() {
		final String url = "https://cdnsciencepub.com/doi/full/10.1139/anc-2018-0028";
		final String resultFile = resultDirectory + "CdnSciencePubScraperUnitURLTest3.bib";
		assertScraperResult(url, null, CdnSciencePubScraper.class, resultFile);
	}

	@Test
	public void url4TestRun() {
		final String url = "https://cdnsciencepub.com/doi/abs/10.1139/o59-099";
		final String resultFile = resultDirectory + "CdnSciencePubScraperUnitURLTest4.bib";
		assertScraperResult(url, null, CdnSciencePubScraper.class, resultFile);
	}
}
