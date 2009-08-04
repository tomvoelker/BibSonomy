package org.bibsonomy.scraper.url.kde.casesjournal;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author claus
 * @version $Id$
 */
public class CasesJournalScraperTest {

	/**
	 * starts URL test with id url_154
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_154"));
	}
}
