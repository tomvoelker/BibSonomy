package scraper.url.kde.nasa.ads;

import org.junit.Ignore;
import org.junit.Test;

import scraper.UnitTestRunner;
import static org.junit.Assert.assertTrue;

/**
 * Scraper URL tests #22 #23 for NasaAdsScraper
 * @author tst
 *
 */
public class NasaAdsScraperTest {
	
	/**
	 * starts URL test with id url_22
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_22"));
	}

	/**
	 * starts URL test with id url_23
	 */
	@Test
	@Ignore
	public void url2TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_23"));
	}
	
}
