package org.bibsonomy.scraper.id.kde.isbn;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for ISBNScraper class (no url test)
 * @author tst
 * @version $Id$
 */
public class ISBNScraperTest {


	
	/**
	 * Tests {@link ISBNScraper#supportsScrapingContext(org.bibsonomy.scraper.ScrapingContext)}
	 */
	@Test
	public void testSupportsScrapingContext() {
		final ISBNScraper scraper = new ISBNScraper();
		
		Assert.assertTrue(scraper.supportsScrapingContext(ISBNScraper.getTestContext()));
		
	}
}
