
package org.bibsonomy.scraper.url.kde.jap;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author hagen
 *
 */
public class JAPScraperTest {
	/**
	 * starts URL test with id url_211
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_211"));
	}
}
