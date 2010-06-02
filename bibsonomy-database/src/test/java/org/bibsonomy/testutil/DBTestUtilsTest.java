package org.bibsonomy.testutil;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.bibsonomy.database.common.params.beans.TagIndex;
import org.junit.Test;

/**
 * @author dzo
 * @version $Id$
 */
public class DBTestUtilsTest {
	
	private static final String TAG_STRING_1 = "testtag";
	private static final String TAG_STRING_2 = "tag2";
	private static final String TAG_STRING_3 = "test2";

	/**
	 * tests {@link DBTestUtils#getTagIndex(String...)}
	 */
	@Test
	public void tagIndexBuild() {		
		final List<TagIndex> index = DBTestUtils.getTagIndex(TAG_STRING_1, TAG_STRING_2);
		assertEquals(2, index.size());
		
		final TagIndex firstIndex = index.get(0);
		assertEquals(TAG_STRING_1, firstIndex.getTagName());
		assertEquals(1, firstIndex.getIndex());
		
		final TagIndex secondIndex = index.get(1);
		assertEquals(TAG_STRING_2, secondIndex.getTagName());
		assertEquals(2, secondIndex.getIndex());
	}
	
	/**
	 * tests {@link DBTestUtils#addToTagIndex(List, String...)}
	 */
	@Test
	public void tagIndexAdd() {
		final List<TagIndex> index = DBTestUtils.getTagIndex(TAG_STRING_1, TAG_STRING_2);
		DBTestUtils.addToTagIndex(index, TAG_STRING_3);
		
		final TagIndex thirdIndex = index.get(2);
		assertEquals(TAG_STRING_3, thirdIndex.getTagName());
		assertEquals(3, thirdIndex.getIndex());
	}
}
