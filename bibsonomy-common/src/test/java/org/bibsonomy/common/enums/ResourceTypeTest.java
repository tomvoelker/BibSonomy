package org.bibsonomy.common.enums;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class ResourceTypeTest {

	/**
	 * tests toString
	 */
	@Test
	public void testToString() {
		assertEquals("bookmark", ResourceType.BOOKMARK.getLabel());
		assertEquals("bibtex", ResourceType.BIBTEX.getLabel());
		assertEquals("all", ResourceType.ALL.getLabel());
	}
}