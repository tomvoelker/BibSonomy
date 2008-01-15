package scraper.url.kde.aip;

import org.junit.Ignore;
import org.junit.Test;

import scraper.UnitTestRunner;
import static org.junit.Assert.assertTrue;

/**
 * Scraper URL tests #8 #9 for AipScitationScraper
 * @author tst
 *
 */
public class AipScitationScraperTest {
	
	/**
	 * starts URL test with id url_8
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_8"));
	}

	/**
	 * starts URL test with id url_9
	 */
	@Test
	@Ignore
	public void url2TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_9"));
	}

}
