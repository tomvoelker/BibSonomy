package org.bibsonomy.scraper.InformationExtraction;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class IEScraperTest {

	@Test
	public void testScrape() {
		final ScrapingContext testContext = IEScraper.getTestContext();

		final IEScraper scraper = new IEScraper();

		try {
			final boolean scrape = scraper.scrape(testContext);
		} catch (final ScrapingException ex) {
			ex.printStackTrace();
			fail(ex.getMessage());
		}

	}

	public static void main(String[] args) {


		final ScrapingContext testContext = IEScraper.getTestContext();

		final IEScraper scraper = new IEScraper();

		try {
			final boolean scrape = scraper.scrape(testContext);
		} catch (final ScrapingException ex) {
			ex.printStackTrace();
		}
		
	

	}


}
