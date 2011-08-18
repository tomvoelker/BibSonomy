package org.bibsonomy.scraper.url.kde.taylorandfrancis;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Test;


/**
 * @author schwass
 * @version $Id$
 */
public class TaylorAndFrancisScraperTest {

	/**
	 * 
	 */
	@Test
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_197"));
	}

	/**
	 * 
	 */
	@Test
	public void url2TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_198"));
	}
	
}
