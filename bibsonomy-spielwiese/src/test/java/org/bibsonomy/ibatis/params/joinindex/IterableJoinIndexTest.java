package org.bibsonomy.ibatis.params.joinindex;

import java.util.Iterator;

import junit.framework.TestCase;

import org.bibsonomy.ibatis.TestHelper;
import org.bibsonomy.ibatis.params.generic.ByTagNames;

public class IterableJoinIndexTest extends TestCase {

	/**
	 * Tests the index-pairs.
	 */
	public void testIt() {
		final ByTagNames btn = TestHelper.getDefaultBookmarkByTagNames();
		// btn.addTagName("test");

		final IterableJoinIndex iji = new IterableJoinIndex(btn.getTagIndex());
		int i = 1;
		for (final Iterator<JoinIndex> iter = iji.iterator(); iter.hasNext(); ) {
			final JoinIndex index = iter.next();
			assertEquals(i, index.getIndex1());
			assertEquals(++i, index.getIndex2());
		}
	}
}