package org.bibsonomy.scraper.url.kde.oxfordjournals;

import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * 
 *
 * @author Haile
 */
@Category(RemoteTest.class)
public class OxfordJournalsScraperTest {
	/**
	 * starts URL test with id url_277
	 */
	@Test
	public void urlTestRun(){
		UnitTestRunner.runSingleTest("url_277");
	}
	
}
