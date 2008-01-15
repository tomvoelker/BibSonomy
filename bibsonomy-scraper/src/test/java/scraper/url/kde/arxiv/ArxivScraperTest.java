package scraper.url.kde.arxiv;

import org.junit.Ignore;
import org.junit.Test;

import scraper.UnitTestRunner;
import static org.junit.Assert.assertTrue;

/**
 * Scraper URL tests #10 for ArxivScraper
 * @author tst
 *
 */
public class ArxivScraperTest {
	
	/**
	 * starts URL test with id url_10
	 */
	@Test
	@Ignore
	public void urlTestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_10"));
	}
	
}
