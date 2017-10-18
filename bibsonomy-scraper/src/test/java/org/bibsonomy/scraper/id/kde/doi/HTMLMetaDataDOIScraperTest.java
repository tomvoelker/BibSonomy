package org.bibsonomy.scraper.id.kde.doi;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Test for HTMLMetaDataDOIScraper
 *
 * @author Johannes
 */
@Category(RemoteTest.class)
public class HTMLMetaDataDOIScraperTest {

	@Test
	public void testMetaData() throws ScrapingException, MalformedURLException {
		URL testURL;
		testURL = new URL("https://www.biorxiv.org/content/early/2017/10/06/199430");
		String doi = new HTMLMetaDataDOIScraper().getDoiFromMetaData(testURL);
		assertEquals("10.1101/199430", doi);
	}

	@Test
	public void testURL() throws ScrapingException, MalformedURLException {
		URL testURL;
		testURL = new URL(
				"http://journals.sagepub.com/doi/abs/10.1177/0165551512438353#articleCitationDownloadContainer");
		String doi = HTMLMetaDataDOIScraper.getDoiFromURL(testURL);
		assertEquals("10.1177/0165551512438353", doi);
	}

	@Test
	public void testWebPage() throws ScrapingException, MalformedURLException {
		URL testURL;
		testURL = new URL("https://link.springer.com/book/10.1007/978-3-319-60492-3#about");
		String doi = HTMLMetaDataDOIScraper.getDoiFromWebPage(testURL);
		assertEquals("10.1007/978-3-319-60492-3", doi);
	}

	@Test
	public void urlTest1() throws ScrapingException, MalformedURLException {
		final ScrapingContext sc = new ScrapingContext(new URL("https://link.springer.com/book/10.1007/978-3-319-60492-3#about"));
		HTMLMetaDataDOIScraper scraper = new HTMLMetaDataDOIScraper();

		assertFalse(scraper.scrape(sc));
		assertEquals("10.1007/978-3-319-60492-3", sc.getSelectedText());
	}
}
