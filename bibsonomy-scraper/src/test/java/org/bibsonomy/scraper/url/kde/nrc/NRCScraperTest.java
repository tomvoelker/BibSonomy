package org.bibsonomy.scraper.url.kde.nrc;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.junit.Test;

/**
 * TODO: add documentation to this class
 *
 * @author rja
 */
public class NRCScraperTest {


	/**
	 * starts URL test with id url_312 for the host
	 * http://www.nrcresearchpress.com/
	 */
	@Test
	public void url3TestRun() {
		final String url = "http://www.nrcresearchpress.com/doi/abs/10.1139/o59-099";
		final String resultFile = "APHAScraperUnitURLTest3.bib";
		assertScraperResult(url, null, NRCScraper.class, resultFile);
	}
}
