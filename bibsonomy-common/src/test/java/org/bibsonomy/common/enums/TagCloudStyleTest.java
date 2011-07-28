package org.bibsonomy.common.enums;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class TagCloudStyleTest {

	/**
	 * tests getStyle
	 */
	@Test
	public void getStyle() {
		assertEquals(TagCloudStyle.CLOUD, TagCloudStyle.getStyle(0));
		assertEquals(TagCloudStyle.LIST, TagCloudStyle.getStyle(1));

		for (final int invalid : new int[] { -1, 2, 42, }) {
			try {
				TagCloudStyle.getStyle(invalid);
				fail("Should throw exception");
			} catch (final RuntimeException ignore) {
			}
		}
	}
}