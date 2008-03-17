package org.bibsonomy.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class TagStringUtilsTest {

	/**
	 * tests cleanTags
	 */
	@Test
	public void cleanTags() {
		assertEquals("computer_algebra maple math", TagStringUtils.cleanTags("computer algebra, maple, math", true, ",", "_"));
		assertEquals("computer algebra, maple, math", TagStringUtils.cleanTags("computer algebra, maple, math", false, ",", "_"));
	}
}