package scraper.url.kde.spires;

import org.junit.Ignore;
import org.junit.Test;

import scraper.UnitTestRunner;
import static org.junit.Assert.assertTrue;

/**
 * Scraper URL tests #29 for SpiresScraper
 * @author tst
 *
 */
public class SpiresScraperTest {
	
	/**
	 * starts URL test with id url_29
	 */
	@Test
	@Ignore
	public void urlTestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_29"));
	}
	
}