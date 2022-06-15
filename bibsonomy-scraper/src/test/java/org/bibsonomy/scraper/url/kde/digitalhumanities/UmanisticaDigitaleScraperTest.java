package org.bibsonomy.scraper.url.kde.digitalhumanities;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;
import org.bibsonomy.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(RemoteTest.class)
public class UmanisticaDigitaleScraperTest {
	String resultDirectory = "digitalhumanities/umanisticadigitale/";

	@Test
	public void urlTest1Run(){
		final String url = "https://umanisticadigitale.unibo.it/article/view/10965";
		final String resultFile = resultDirectory + "UmanisticaDigitaleScraperUnitURLTest1.bib";
		assertScraperResult(url, UmanisticaDigitaleScraper.class, resultFile);
	}

}
