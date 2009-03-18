package org.bibsonomy.scraper.InformationExtraction;

import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.Assert;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class IEScraperTest {

	private final String expectedBibtex = 
		"@misc{ieKey,\n" +
		"booktitle = {Research Challenges in Ubiquitous Knowledge Discovery. Next Generation of Data Mining(Chapman & Hall / Crc Data Mining and Knowledge Discovery Series), Chapman & Hall / CRC},\n" +
		"year = {2008},\n" +
		"date = {2008},\n" +
		"title = {Michael May and Bettina Berendt and Antoine Cornuejols and Joao Gama and Fosca Giannotti and Andreas Hotho and Donato Malerba and Ernestina Menesalvas and Katharina Morik and Rasmus Pedersen and Lorenza Saitta and Yucel Saygin and Assaf Schuster and Koen Vanhoof}\n" +
		",url = {http://www.example.com/reasearch_challenges.html}\n" +
		"}";

	@Test
	public void testScrape() {
		final ScrapingContext sc = IEScraper.getTestContext();
		try {
			sc.setUrl(new URL("http://www.example.com/reasearch_challenges.html"));
		} catch (MalformedURLException ex) {
			fail(ex.getMessage());
		}

		final IEScraper scraper = new IEScraper();

		try {
			final boolean scrape = scraper.scrape(sc);
			Assert.assertTrue(scrape);
		} catch (final ScrapingException ex) {
			ex.printStackTrace();
			fail(ex.getMessage());
		}

		final String bibtex = sc.getBibtexResult();

		Assert.assertEquals(expectedBibtex, bibtex);

	}

}
