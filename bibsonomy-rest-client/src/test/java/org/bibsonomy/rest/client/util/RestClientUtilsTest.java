package org.bibsonomy.rest.client.util;

import static org.junit.Assert.assertTrue;

import org.junit.Test;


/**
 * @author dzo
 * @version $Id$
 */
public class RestClientUtilsTest {

	@Test
	public void testRestClientVersion() {
		assertTrue(RestClientUtils.getRestClientVersion().startsWith("2."));
	}
}
