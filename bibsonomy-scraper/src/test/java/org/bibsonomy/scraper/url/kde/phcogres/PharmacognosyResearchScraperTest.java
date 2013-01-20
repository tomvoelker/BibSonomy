
package org.bibsonomy.scraper.url.kde.phcogres;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author hagen
 *
 */
public class PharmacognosyResearchScraperTest {
	
	/**
	 * starts URL test with id url_251
	 */
	@Test
	@Ignore
	public void urlTestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_251"));
	}
}
