package scraper.url.kde.ingenta;

import org.junit.Ignore;
import org.junit.Test;

import scraper.UnitTestRunner;
import static org.junit.Assert.assertTrue;

/**
 * Scraper URL tests #15 for IngentaconnectScraper
 * @author tst
 *
 */
public class IngentaconnectScraperTest {
	
	/**
	 * starts URL test with id url_15
	 */
	@Test
	@Ignore
	public void urlTestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_15"));
	}
	
}
