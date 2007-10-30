package org.bibsonomy.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

/**
 * Testcase for the StringUtils class
 */
public class StringUtilsTest {

	/**
	 * tests getStringFromList what else ?
	 */
	@Test
	public void getStringFromList() {
		assertEquals("[]", StringUtils.getStringFromList(Collections.<String> emptyList()));

		final List<String> someStrings = new ArrayList<String>();
		for (final int i : new int[] { 1, 2, 3 })
			someStrings.add("test" + i);
		assertEquals("[test1,test2,test3]", StringUtils.getStringFromList(someStrings));
	}
}