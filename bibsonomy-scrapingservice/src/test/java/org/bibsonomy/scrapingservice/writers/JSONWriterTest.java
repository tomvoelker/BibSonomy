package org.bibsonomy.scrapingservice.writers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.bibsonomy.scraper.UrlCompositeScraper;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class JSONWriterTest {

	@Test
	public void testWrite() {
		
		final JSONWriter writer = new JSONWriter(System.out);
		final UrlCompositeScraper scraper = new UrlCompositeScraper();
		
		System.out.println("------------------------------------------------------");
		try {
			writer.write(scraper.getUrlPatterns());
			writer.close();

		} catch (UnsupportedEncodingException e) {
			Assert.fail(e.getMessage());
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		System.out.println("------------------------------------------------------");
		
		
	}
}

