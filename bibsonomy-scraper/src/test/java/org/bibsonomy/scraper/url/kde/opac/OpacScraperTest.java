package org.bibsonomy.scraper.url.kde.opac;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 * Tests url #63
 * 
 * @author daill
 * @version $Id$
 */
public class OpacScraperTest {
	/**
	 * starts URL test with id url_65
	 */
	@Test
	@Ignore
	public void urlTest1Run(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_65"));
	}
}
