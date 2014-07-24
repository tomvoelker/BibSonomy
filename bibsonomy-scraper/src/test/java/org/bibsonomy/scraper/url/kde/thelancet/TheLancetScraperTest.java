package org.bibsonomy.scraper.url.kde.thelancet;

import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 *
 * @author Haile
 */
@Category(RemoteTest.class)
public class TheLancetScraperTest {
	/**
	 * url_278
	 */
	@Test
	public void urlTestRun(){
		UnitTestRunner.runSingleTest("url_278");
	}

}
