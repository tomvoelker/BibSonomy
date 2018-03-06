package org.bibsonomy.scraper.generic;

import static org.bibsonomy.scraper.junit.RemoteTestAssert.assertScraperResult;

import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * tests the HighwireScraper
 *
 * @author rja
 */
@Category(RemoteTest.class)
public class HighwireScraperTest {

	/**
	 * Test method for {@link org.bibsonomy.scraper.generic.HighwireScraper#scrape(org.bibsonomy.scraper.ScrapingContext)}.
	 */
	@Test
	public void testScrape1() {
		assertScraperResult("http://www.pnas.org/content/115/4/E639", null, HighwireScraper.class, "HighwireScraperTest1.bib");
	}
	
	/**
	 * Test method for {@link org.bibsonomy.scraper.generic.HighwireScraper#scrape(org.bibsonomy.scraper.ScrapingContext)}.
	 */
	@Test
	public void testScrape2() {
		assertScraperResult("http://err.ersjournals.com/content/27/147/170106", null, HighwireScraper.class, "HighwireScraperTest2.bib");
	}

	/**
	 * Test method for {@link org.bibsonomy.scraper.generic.HighwireScraper#scrape(org.bibsonomy.scraper.ScrapingContext)}.
	 */
	@Test
	public void testScrape3() {
		assertScraperResult("http://eel.ecsdl.org/content/4/1/A4.abstract", null, HighwireScraper.class, "HighwireScraperTest3.bib");
	}
	
	/**
	 * Test method for {@link org.bibsonomy.scraper.generic.HighwireScraper#scrape(org.bibsonomy.scraper.ScrapingContext)}.
	 */
	@Test
	public void testScrape4() {
		assertScraperResult("http://horttech.ashspublications.org/content/28/1/10.abstract", null, HighwireScraper.class, "HighwireScraperTest4.bib");
	}
	
	/**
	 * Test method for {@link org.bibsonomy.scraper.generic.HighwireScraper#scrape(org.bibsonomy.scraper.ScrapingContext)}.
	 */
	@Test
	public void testScrape5() {
		assertScraperResult("https://pubs.geoscienceworld.org/paleobiol/article-abstract/43/4/620/520315/sexual-dimorphism-and-sexual-selection-in", null, HighwireScraper.class, "HighwireScraperTest5.bib");
	}
}
