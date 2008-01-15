package scraper.url.kde.mathscinet;

import org.junit.Ignore;
import org.junit.Test;

import scraper.UnitTestRunner;
import static org.junit.Assert.assertTrue;

/**
 * Scraper URL tests #20 #21 for MathSciNetScraper
 * @author tst
 *
 */
public class MathSciNetScraperTest {

	/**
	 * starts URL test with id url_20
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_20"));
	}

	/**
	 * starts URL test with id url_21
	 */
	@Test
	@Ignore
	public void url2TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_21"));
	}

}
