package org.bibsonomy.model.util;

import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class PostUtilsTest {

	@Test
	public void testSetGroupIdsPostOfQextendsResourceBoolean() {
		final Post<Bookmark> post = new Post<Bookmark>();
		final Set<Group> groups = new HashSet<Group>();
		
		final Group group1 = new Group(0);
		final Group group2 = new Group("kde");
		final Group group3 = new Group(7);
		
		groups.add(group1);
		groups.add(group2);
		groups.add(group3);
		post.setGroups(groups);
		
		/*
		 * set to non-spammer
		 */
		PostUtils.setGroupIds(post, false);
		Assert.assertEquals(0, group1.getGroupId());
		// FIXME: bug in UserUtils.getGroupId	
//		Assert.assertEquals(GroupID.INVALID.getId(), group2.getGroupId());
		Assert.assertEquals(7, group3.getGroupId());
		
		/*
		 * set to spammer
		 */
		PostUtils.setGroupIds(post, true);
		Assert.assertEquals(-2147483648, group1.getGroupId());
		// FIXME: bug in UserUtils.getGroupId
//		Assert.assertEquals(0, group2.getGroupId());
		Assert.assertEquals(-2147483641, group3.getGroupId());		

		/*
		 * set to spammer
		 */
		PostUtils.setGroupIds(post, true);
		Assert.assertEquals(-2147483648, group1.getGroupId());
		// FIXME: bug in UserUtils.getGroupId
//		Assert.assertEquals(0, group2.getGroupId());
		Assert.assertEquals(-2147483641, group3.getGroupId());		

		/*
		 * set to non-spammer
		 */
		PostUtils.setGroupIds(post, false);
		Assert.assertEquals(0, group1.getGroupId());
		// FIXME: bug in UserUtils.getGroupId	
//		Assert.assertEquals(GroupID.INVALID.getId(), group2.getGroupId());
		Assert.assertEquals(7, group3.getGroupId());
	}

}
