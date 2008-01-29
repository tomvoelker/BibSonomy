package scraper.url.kde.nature;

import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import scraper.UnitTestRunner;

/**
 * Scraper URL tests #45 for NatureScraper
 * @author tst
 */
public class NatureScraperTest {

	/**
	 * starts URL test with id url_45
	 */
	@Test
	@Ignore
	public void urlTest1Run(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_45"));
	}
		
}
