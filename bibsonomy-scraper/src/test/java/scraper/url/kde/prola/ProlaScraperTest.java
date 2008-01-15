package scraper.url.kde.prola;

import org.junit.Ignore;
import org.junit.Test;

import scraper.UnitTestRunner;
import static org.junit.Assert.assertTrue;

/**
 * Scraper URL tests #24 #25 for ProlaScraper
 * @author tst
 *
 */
public class ProlaScraperTest {
	
	/**
	 * starts URL test with id url_24
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_24"));
	}

	/**
	 * starts URL test with id url_25
	 */
	@Test
	@Ignore
	public void url2TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_25"));
	}

}
