package org.bibsonomy.scraper.ie;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class CRFSingletonTest {

	@Test
	public void testGetCrf() {
		try {
			Assert.assertNotNull(new CRFSingleton().getCrf());
		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail(ex.getMessage());
		}
	}

}
