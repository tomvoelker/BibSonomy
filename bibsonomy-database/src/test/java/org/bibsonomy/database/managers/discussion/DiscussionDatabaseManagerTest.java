package org.bibsonomy.database.managers.discussion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.model.Comment;
import org.bibsonomy.model.DiscussionItem;
import org.bibsonomy.model.Review;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author dzo
 * @version $Id$
 */
public class DiscussionDatabaseManagerTest extends AbstractDatabaseManagerTest {
	
	private static DiscussionDatabaseManager discusssionDb;
	
	public static final String HASH_WITH_RATING = "097248439469d8f5a1e7fad6b02cbfcd";
	
	public static final List<Integer> USER_NOT_LOGGED_IN_VISIBLE_GROUPS = new LinkedList<Integer>(Arrays.asList(GroupID.PUBLIC.getId()));
	public static final List<Integer> USERNAME_1_VISIBLE_GROUPS = new LinkedList<Integer>(Arrays.asList(GroupID.PUBLIC.getId(), TESTGROUP1_ID, TESTGROUP2_ID, TESTGROUP3_ID));
	public static final List<Integer> USERNAME_2_VISIBLE_GROUPS = new LinkedList<Integer>(Arrays.asList(GroupID.PUBLIC.getId(), TESTGROUP1_ID));
	
	@BeforeClass
	public static void setupManager() {
		discusssionDb = DiscussionDatabaseManager.getInstance();
	}
	
	@Test
	public void testGetDiscussionItemsForPost() {
		List<DiscussionItem> items = discusssionDb.getDiscussionSpaceForResource(HASH_WITH_RATING, ReviewDatabaseManagerTest.USERNAME_1, USERNAME_1_VISIBLE_GROUPS, this.dbSession);
		assertEquals(4, items.size());
		
		assertTrue(items.get(0) instanceof Comment);
		assertTrue(items.get(1) instanceof Comment);
		assertTrue(items.get(2) instanceof Comment);
		assertTrue(items.get(3) instanceof Review);

		items = discusssionDb.getDiscussionSpaceForResource(HASH_WITH_RATING, ReviewDatabaseManagerTest.USERNAME_2, USERNAME_2_VISIBLE_GROUPS, this.dbSession);
		assertEquals(3, items.size());

		items = discusssionDb.getDiscussionSpaceForResource(ReviewDatabaseManagerTest.HASH, ReviewDatabaseManagerTest.USERNAME_1, USERNAME_1_VISIBLE_GROUPS, this.dbSession);
		assertEquals(0, items.size());
		
		items = discusssionDb.getDiscussionSpaceForResource(HASH_WITH_RATING, null, USER_NOT_LOGGED_IN_VISIBLE_GROUPS, this.dbSession);
	}
}
