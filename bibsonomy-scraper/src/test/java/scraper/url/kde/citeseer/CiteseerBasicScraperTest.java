package scraper.url.kde.citeseer;

import org.junit.Ignore;
import org.junit.Test;

import scraper.UnitTestRunner;
import static org.junit.Assert.assertTrue;

/**
 * Scraper URL tests #12 for CiteseerBasicScraper
 * @author tst
 *
 */
public class CiteseerBasicScraperTest {
	
	/**
	 * starts URL test with id url_12
	 */
	@Test
	@Ignore
	public void urlTestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_12"));
	}
	
}
