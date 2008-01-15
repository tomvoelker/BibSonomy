package scraper.url.kde.bibsonomy;

import org.junit.Ignore;
import org.junit.Test;

import scraper.UnitTestRunner;
import static org.junit.Assert.assertTrue;

/**
 * Scraper URL tests #38 #39 for BibSonomyScraper
 * @author tst
 *
 */
public class BibSonomyScraperTest {
	
	/**
	 * starts URL test with id url_38
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_38"));
	}
	
	/**
	 * starts URL test with id url_39
	 */
	@Test
	@Ignore
	public void url2TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_39"));
	}

	
}
