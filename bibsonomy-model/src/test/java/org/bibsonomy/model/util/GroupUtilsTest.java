package org.bibsonomy.model.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.bibsonomy.model.Group;
import static org.bibsonomy.util.ValidationUtils.present;
import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class GroupUtilsTest {

	/**
	 * tests the groups
	 */
	@Test
	public void getGroup() {
		for (final Group group : new Group[] { GroupUtils.getPublicGroup(), GroupUtils.getPrivateGroup(), GroupUtils.getFriendsGroup(), GroupUtils.getInvalidGroup() }) {
			assertNotNull(group);
			assertTrue(present(group.getName()));
			assertTrue(present(group.getDescription()));
			assertTrue(present(group.getGroupId()));
			assertTrue(present(group.getPrivlevel()));
		}

		// no caching please (due to possible side effects)
		final Group g1 = GroupUtils.getPublicGroup();
		final Group g2 = GroupUtils.getPublicGroup();
		assertNotSame(g1, g2);
	}
}