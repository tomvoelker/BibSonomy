package org.bibsonomy.common.enums;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class TagCloudSortTest {

	/**
	 * tests getSort
	 */
	@Test
	public void getSort() {
		assertEquals(TagCloudSort.ALPHA, TagCloudSort.getSort(0));
		assertEquals(TagCloudSort.FREQ, TagCloudSort.getSort(1));

		for (final int invalid : new int[] { -1, 2, 42 }) {
			try {
				TagCloudSort.getSort(invalid);
				fail("Should throw exception");
			} catch (final RuntimeException ignore) {
			}
		}
	}
}