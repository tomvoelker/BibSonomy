package org.bibsonomy.scraper.url.kde.oup;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper URL test #371 and #372 for OxfordUniversityPressScraper
 * 
 * @author rja
 */
@Category(RemoteTest.class)
public class OxfordUniversityPressScraperTest {

	/**
	 * starts URL test with id url_371
	 */
	@Test
	public void urlTest1Run() {
		assertScraperResult("https://academic.oup.com/rev/article/22/3/157/1521720", null, OxfordUniversityPressScraper.class, "OxfordUniversityPressUnitURLTest.bib");
	}

	/**
	 * starts URL test with id url_372
	 */
	@Test
	public void urlTest2Run() {
		assertScraperResult("https://academic.oup.com/rev/article/22/3/157/1521720/A-study-of-global-and-local-visibility-as-web", null, OxfordUniversityPressScraper.class, "OxfordUniversityPressUnitURLTest.bib");	    
	}
}
