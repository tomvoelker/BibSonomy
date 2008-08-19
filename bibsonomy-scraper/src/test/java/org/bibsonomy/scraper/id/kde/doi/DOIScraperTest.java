package org.bibsonomy.scraper.id.kde.doi;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

/**
 * Test for DOIScraper class (no url test)
 * @author tst
 * @version $Id$
 */
public class DOIScraperTest {
	
	/**
	 * test getting URL 
	 */
	@Test
	public void getUrlForDoiTest(){
		try {
			assertTrue(DOIScraper.getUrlForDoi("10.1007/11922162").toString().equals("http://www.springerlink.com/index/10.1007/11922162"));
		} catch (IOException ex) {
			assertTrue(false);
		}
	}

}
