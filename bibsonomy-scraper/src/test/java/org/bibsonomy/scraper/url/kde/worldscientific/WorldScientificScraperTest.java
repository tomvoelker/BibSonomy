package org.bibsonomy.scraper.url.kde.worldscientific;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Scraper test for url_279, references and cited by
 * @author Haile
 */
@Category(RemoteTest.class)
public class WorldScientificScraperTest {
	/**
	 * starts URL test with id url_279
	 */
	@Test
	public void urlTestRun1(){
		UnitTestRunner.runSingleTest("url_279");
	}
	@Test
	public void testCitedby() throws Exception {
		final ScrapingContext sc = new ScrapingContext(new URL("http://www.worldscientific.com/doi/pdf/10.1142/S0219622006002271"));
		
		WorldScientificScraper ws = new WorldScientificScraper();
		assertTrue(ws.scrape(sc));
		assertTrue(ws.scrapeCitedby(sc));
		final String cby = sc.getCitedBy();
		assertNotNull(cby);
		assertTrue(cby.length() > 100);
		
		assertEquals("<a class=\"entryAuthor\" href=\"/action/doSearch?Contrib=FANG%2C+XIANYONG\">XIANYONG FANG</a>".trim(), cby.substring(0, 89).trim());
		
		assertTrue(cby.contains("XIANYONG FANG"));
	}
	@Test
	public void testReferences() throws Exception {
		final ScrapingContext sc = new ScrapingContext(new URL("http://www.worldscientific.com/doi/pdf/10.1142/S0219622006002271"));
		
		WorldScientificScraper ws = new WorldScientificScraper();
		assertTrue(ws.scrape(sc));
		assertTrue(ws.scrapeReferences(sc));
		final String references = sc.getReferences();
		assertNotNull(references);
		assertTrue(references.length() > 100);
		
		assertEquals("<li class=\"reference\"> R. Agrawal, Mining Newsgroups using networks arising from social behavior, <i>Proc".trim(), references.substring(0, 105).trim());
		
		assertTrue(references.contains("A. D.   Baxevanis"));
	}


}
