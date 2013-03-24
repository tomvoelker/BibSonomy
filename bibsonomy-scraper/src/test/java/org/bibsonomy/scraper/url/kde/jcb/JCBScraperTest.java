
package org.bibsonomy.scraper.url.kde.jcb;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author hagen
 *
 */
public class JCBScraperTest {
	/**
	 * starts URL test with id url_254
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_254"));
	}
}
