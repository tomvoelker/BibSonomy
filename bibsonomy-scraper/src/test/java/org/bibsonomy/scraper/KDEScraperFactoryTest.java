package org.bibsonomy.scraper;

import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.Assert;

import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class KDEScraperFactoryTest {

	@Test
	@Ignore
	public void testGetScraper() throws MalformedURLException, ScrapingException {
		
		final String urlString = "http://dx.doi.org/10.1017/S0952523808080978";
		
		final CompositeScraper scraper = new KDEScraperFactory().getScraper();
		
		final ScrapingContext sc = new ScrapingContext(new URL(urlString));
		
		scraper.scrape(sc);
		
		Assert.assertTrue(scraper.scrape(sc));
		
		System.out.println(sc.getBibtexResult());
		
	}

}
