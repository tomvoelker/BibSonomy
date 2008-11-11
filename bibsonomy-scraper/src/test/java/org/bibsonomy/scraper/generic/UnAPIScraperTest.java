package org.bibsonomy.scraper.generic;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class UnAPIScraperTest {

	@Test
	public void testScrape() {
		final UnAPIScraper scraper = new UnAPIScraper();
		ScrapingContext scrapingContext = null;
		try {
			scrapingContext = new ScrapingContext(new URL("http://canarydatabase.org/record/488"));
		} catch (MalformedURLException ex) {
			fail(ex.getMessage());
		}

		try {
			scraper.scrape(scrapingContext);
		} catch (ScrapingException ex) {
			fail(ex.getMessage());
		}
	}

}
