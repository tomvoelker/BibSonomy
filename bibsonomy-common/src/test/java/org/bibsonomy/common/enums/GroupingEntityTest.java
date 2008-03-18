package org.bibsonomy.common.enums;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.UnsupportedGroupingException;
import org.junit.Test;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GroupingEntityTest {

	/**
	 * tests getGroupingEntity
	 */
	@Test
	public void testGetGroupingEntity() {
		assertEquals(GroupingEntity.ALL, GroupingEntity.getGroupingEntity("all"));
		assertEquals(GroupingEntity.GROUP, GroupingEntity.getGroupingEntity("group"));
		assertEquals(GroupingEntity.USER, GroupingEntity.getGroupingEntity("user"));
		assertEquals(GroupingEntity.VIEWABLE, GroupingEntity.getGroupingEntity("viewable"));
		assertEquals(GroupingEntity.FRIEND, GroupingEntity.getGroupingEntity("friend"));
		assertEquals(GroupingEntity.ALL, GroupingEntity.getGroupingEntity(" All"));
		assertEquals(GroupingEntity.GROUP, GroupingEntity.getGroupingEntity("GROUP"));
		assertEquals(GroupingEntity.USER, GroupingEntity.getGroupingEntity("uSeR "));
		assertEquals(GroupingEntity.VIEWABLE, GroupingEntity.getGroupingEntity("ViewAble"));
		assertEquals(GroupingEntity.FRIEND, GroupingEntity.getGroupingEntity("FrIend"));

		for (final String test : new String[] { "", " ", null }) {
			try {
				ConceptStatus.getConceptStatus(test);
				fail("Should throw exception");
			} catch (InternServerException ignore) {
			}
		}

		try {
			GroupingEntity.getGroupingEntity("foo bar");
			fail("Should throw exception");
		} catch (final UnsupportedGroupingException ex) {
		}
	}

	/**
	 * We want to make sure that this is the case, because we are relying on it
	 * in our testcases.
	 */
	@Test
	public void testToString() {
		assertEquals("GROUP", GroupingEntity.GROUP.toString());
		assertEquals("USER", GroupingEntity.USER.toString());
		assertEquals("VIEWABLE", GroupingEntity.VIEWABLE.toString());
		assertEquals("ALL", GroupingEntity.ALL.toString());
		assertEquals("FRIEND", GroupingEntity.FRIEND.toString());
	}
}