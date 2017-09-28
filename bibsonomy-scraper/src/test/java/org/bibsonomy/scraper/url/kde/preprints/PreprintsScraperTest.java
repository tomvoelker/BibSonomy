package org.bibsonomy.scraper.url.kde.preprints;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.net.MalformedURLException;
import java.net.URL;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Test for PreprintsScraper (no URL tests)
 *
 * @author Johannes
 */
@Category(RemoteTest.class)
public class PreprintsScraperTest {
	private static final PreprintsScraper SCRAPER = new PreprintsScraper();

	/**
	 * @throws ScrapingException
	 * @throws MalformedURLException 
	 */
	@Test
	public void testScraper1() throws ScrapingException, MalformedURLException {
		final ScrapingContext sc = new ScrapingContext(new URL("https://www.preprints.org/manuscript/201708.0040/v2"));
		assertFalse(SCRAPER.scrape(sc));
		assertEquals("10.20944/preprints201708.0040.v2", sc.getSelectedText());
	}
}
