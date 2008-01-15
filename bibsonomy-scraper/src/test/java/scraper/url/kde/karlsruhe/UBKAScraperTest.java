package scraper.url.kde.karlsruhe;

import org.junit.Ignore;
import org.junit.Test;

import scraper.UnitTestRunner;
import static org.junit.Assert.assertTrue;

/**
 * Scraper URL tests #31 #32 for UBKAScraper
 * @author tst
 *
 */
public class UBKAScraperTest {
	
	/**
	 * starts URL test with id url_31
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_31"));
	}

	/**
	 * starts URL test with id url_32
	 */
	@Test
	@Ignore
	public void url2TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_32"));
	}

}
