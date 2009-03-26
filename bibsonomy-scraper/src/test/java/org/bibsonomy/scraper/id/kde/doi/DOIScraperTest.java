package org.bibsonomy.scraper.id.kde.doi;

import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.Assert;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test for DOIScraper class (no url test)
 * @author tst
 * @version $Id$
 */
public class DOIScraperTest {

	
	
	@Test
	@Ignore
	public void testScraper() throws ScrapingException, MalformedURLException {
		final ScrapingContext sc = new ScrapingContext(new URL("http://dx.doi.org/10.1007/11922162"));
		final DOIScraper scraper = new DOIScraper();
		
		Assert.assertFalse(scraper.scrape(sc));
		
		Assert.assertEquals("http://www.springerlink.com/index/10.1007/11922162", sc.getUrl().toString());
	}

}
