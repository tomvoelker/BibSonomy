package org.bibsonomy.scraper.url.kde.isi;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author claus
 * @version $Id$
 */
public class IsiScraperTest {

	/**
	 * starts URL test with id url_151
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_151"));
	}
}
