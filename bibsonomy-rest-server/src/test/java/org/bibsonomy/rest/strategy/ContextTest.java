package org.bibsonomy.rest.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.strategy.groups.AddGroupStrategy;
import org.bibsonomy.rest.strategy.groups.AddUserToGroupStrategy;
import org.bibsonomy.rest.strategy.groups.DeleteGroupStrategy;
import org.bibsonomy.rest.strategy.groups.GetGroupStrategy;
import org.bibsonomy.rest.strategy.groups.GetListOfGroupsStrategy;
import org.bibsonomy.rest.strategy.groups.GetUserListOfGroupStrategy;
import org.bibsonomy.rest.strategy.groups.RemoveUserFromGroupStrategy;
import org.bibsonomy.rest.strategy.groups.UpdateGroupDetailsStrategy;
import org.bibsonomy.rest.strategy.posts.GetListOfPostsStrategy;
import org.bibsonomy.rest.strategy.posts.GetNewPostsStrategy;
import org.bibsonomy.rest.strategy.posts.GetPopularPostsStrategy;
import org.bibsonomy.rest.strategy.posts.standard.DeleteStandardPostStrategy;
import org.bibsonomy.rest.strategy.posts.standard.PutStandardPostStrategy;
import org.bibsonomy.rest.strategy.posts.standard.references.DeleteReferencesStrategy;
import org.bibsonomy.rest.strategy.posts.standard.references.PostReferencesStrategy;
import org.bibsonomy.rest.strategy.tags.GetListOfTagsStrategy;
import org.bibsonomy.rest.strategy.tags.GetTagDetailsStrategy;
import org.bibsonomy.rest.strategy.users.DeletePostStrategy;
import org.bibsonomy.rest.strategy.users.DeleteUserStrategy;
import org.bibsonomy.rest.strategy.users.GetPostDetailsStrategy;
import org.bibsonomy.rest.strategy.users.GetUserListStrategy;
import org.bibsonomy.rest.strategy.users.GetUserPostsStrategy;
import org.bibsonomy.rest.strategy.users.GetUserStrategy;
import org.bibsonomy.rest.strategy.users.PostPostStrategy;
import org.bibsonomy.rest.strategy.users.PostUserStrategy;
import org.bibsonomy.rest.strategy.users.PutPostStrategy;
import org.bibsonomy.rest.strategy.users.PutUserStrategy;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class ContextTest extends AbstractContextTest {
	private static final String NOT_SPLITTED_MSG = "tag parameters are not correctly splitted!";
	private Map<String, String[]> parameterMap;
	
	
	@Before
	public void setUpMap() {
		this.parameterMap = new HashMap<String, String[]>();
	}

	@Test
	public void testGetSimpleTags() {
		this.parameterMap.put("tags", new String[] { "foo bar" });
		final Context ctx = new Context(this.is, this.db, HttpMethod.GET, "/users/egal/posts", this.parameterMap, null, null);

		final List<String> tags = ctx.getTags("tags");
		assertTrue(NOT_SPLITTED_MSG, tags.contains("foo"));
		assertTrue(NOT_SPLITTED_MSG, tags.contains("bar"));
		assertEquals(NOT_SPLITTED_MSG, 2, tags.size());
	}

	@Test
	public void testGetTags() {
		this.parameterMap.put("tags", new String[] { "foo bar ->subtags -->transitiveSubtags supertags-> transitiveSupertags--> <->correlated" });
		final Context ctx = new Context(this.is, this.db, HttpMethod.GET, "/users/egal/posts", this.parameterMap, null, null);

		final List<String> tags = ctx.getTags("tags");
		assertTrue(NOT_SPLITTED_MSG, tags.contains("foo"));
		assertTrue(NOT_SPLITTED_MSG, tags.contains("bar"));
		assertTrue(NOT_SPLITTED_MSG, tags.contains("->subtags"));
		assertTrue(NOT_SPLITTED_MSG, tags.contains("-->transitiveSubtags"));
		assertTrue(NOT_SPLITTED_MSG, tags.contains("supertags->"));
		assertTrue(NOT_SPLITTED_MSG, tags.contains("transitiveSupertags-->"));
		assertTrue(NOT_SPLITTED_MSG, tags.contains("<->correlated"));
		assertEquals(NOT_SPLITTED_MSG, 7, tags.size());
	}
	
	@Test
	public void testGetListOfGroupsStrategy() throws Exception {
		final Context c = new Context(this.is, db, HttpMethod.GET, "/groups", new HashMap<Object, Object>(), null, null);
		assertTrue("failure initializing GetListOfGroupsStrategy", c.getStrategy() instanceof GetListOfGroupsStrategy);
	}

	@Test
	public void testAddGroupStrategy() throws Exception {
		final Context c = new Context(this.is, db, HttpMethod.POST, "/groups", new HashMap<Object, Object>(), null, null);
		assertTrue("failure initializing AddGroupStrategy", c.getStrategy() instanceof AddGroupStrategy);
	}

	@Test
	public void testGetDetailsOfGroupStrategy() throws Exception {
		final Context c = new Context(this.is, db, HttpMethod.GET, "/groups/testgroup", new HashMap<Object, Object>(), null, null);
		assertTrue("failure initializing GetGroupStrategy", c.getStrategy() instanceof GetGroupStrategy);
	}

	@Test
	public void testUpdateGroupDetailsStrategy() throws Exception {
		final Context c = new Context(this.is, db, HttpMethod.PUT, "/groups/testgroup", new HashMap<Object, Object>(), null, null);
		assertTrue("failure initializing UpdateGroupDetailsStrategy", c.getStrategy() instanceof UpdateGroupDetailsStrategy);
	}

	@Test
	public void testDeleteGroupStrategy() throws Exception {
		final Context c = new Context(this.is, db, HttpMethod.DELETE, "/groups/testgroup", new HashMap<Object, Object>(), null, null);
		assertTrue("failure initializing DeleteGroupStrategy", c.getStrategy() instanceof DeleteGroupStrategy);
	}

	@Test
	public void testGetUserListOfGroupStrategy() throws Exception {
		final Context c = new Context(this.is, db, HttpMethod.GET, "/groups/testgroup/users", new HashMap<Object, Object>(), null, null);
		assertTrue("failure initializing GetUserListOfGroupStrategy", c.getStrategy() instanceof GetUserListOfGroupStrategy);
	}

	@Test
	public void testAddUserToGroupStrategy() throws Exception {
		final Context c = new Context(this.is, db, HttpMethod.POST, "/groups/testgroup/users", new HashMap<Object, Object>(), null, null);
		assertTrue("failure initializing AddUserToGroupStrategy", c.getStrategy() instanceof AddUserToGroupStrategy);
	}

	@Test
	public void testRemoveUserFromGroupStrategy() throws Exception {
		final Context c = new Context(this.is, db, HttpMethod.DELETE, "/groups/testgroup/users/testuser", new HashMap<Object, Object>(), null, null);
		assertTrue("failure initializing RemoveUserFromGroupStrategy", c.getStrategy() instanceof RemoveUserFromGroupStrategy);
	}
	
	@Test
	public void testGetListOfTagsStrategy() throws Exception {
		final Context c = new Context(this.is, db, HttpMethod.GET, "/tags", new HashMap<Object, Object>(), null, null);
		assertTrue("failure initializing GetListOfTagsStrategy", c.getStrategy() instanceof GetListOfTagsStrategy);
	}

	@Test
	public void testGetTagDetailsStrategy() throws Exception {
		final Context c = new Context(this.is, db, HttpMethod.GET, "/tags/wichtig", new HashMap<Object, Object>(), null, null);
		assertTrue("failure initializing GetTagDetailsStrategy", c.getStrategy() instanceof GetTagDetailsStrategy);
	}
	
	@Test
	public void testGetListOfUsersStrategy() throws Exception {
		final Context ctx = new Context(this.is, this.db, HttpMethod.GET, "/users", new HashMap<Object, Object>(), null, null);
		assertTrue("failure initializing GetUserListStrategy", ctx.getStrategy() instanceof GetUserListStrategy);
	}

	@Test
	public void testPostUserStrategy() throws Exception {
		final Context ctx = new Context(this.is, this.db, HttpMethod.POST, "/users", new HashMap<Object, Object>(), null, null);
		assertTrue("failure initializing PostUserStrategy", ctx.getStrategy() instanceof PostUserStrategy);
	}

	@Test
	public void testGetDetailsOfUserStrategy() throws Exception {
		final Context ctx = new Context(this.is, this.db, HttpMethod.GET, "/users/testuser", new HashMap<Object, Object>(), null, null);
		assertTrue("failure initializing GetUserStrategy", ctx.getStrategy() instanceof GetUserStrategy);
	}

	@Test
	public void testPutDetailsOfUserStrategy() throws Exception {
		final Context ctx = new Context(this.is, this.db, HttpMethod.PUT, "/users/testuser", new HashMap<Object, Object>(), null, null);
		assertTrue("failure initializing PutUserStrategy", ctx.getStrategy() instanceof PutUserStrategy);
	}

	@Test
	public void testDeleteUserStrategy() throws Exception {
		final Context ctx = new Context(this.is, this.db, HttpMethod.DELETE, "/users/testuser", new HashMap<Object, Object>(), null, null);
		assertTrue("failure initializing DeleteUserStrategy", ctx.getStrategy() instanceof DeleteUserStrategy);
	}

	@Test
	public void testGetUserPostsStrategy() throws Exception {
		final Context ctx = new Context(this.is, this.db, HttpMethod.GET, "/users/testuser/posts", new HashMap<Object, Object>(), null, null);
		assertTrue("failure initializing GetUserPostsStrategy", ctx.getStrategy() instanceof GetUserPostsStrategy);
	}

	@Test
	public void testPostPostStrategy() throws Exception {
		final Context ctx = new Context(this.is, this.db, HttpMethod.POST, "/users/testuser/posts", new HashMap<Object, Object>(), null, null);
		assertTrue("failure initializing PostPostStrategy", ctx.getStrategy() instanceof PostPostStrategy);
	}

	@Test
	public void testGetPostDetailsStrategy() throws Exception {
		final Context ctx = new Context(this.is, this.db, HttpMethod.GET, "/users/testuser/posts/asdfsadf012312", new HashMap<Object, Object>(), null, null);
		assertTrue("failure initializing GetPostDetailsStrategy", ctx.getStrategy() instanceof GetPostDetailsStrategy);
	}

	@Test
	public void testPutPostStrategy() throws Exception {
		final Context ctx = new Context(this.is, this.db, HttpMethod.PUT, "/users/testuser/posts/asdfsadf012312", new HashMap<Object, Object>(), null, null);
		assertTrue("failure initializing PutPostStrategy", ctx.getStrategy() instanceof PutPostStrategy);
	}

	@Test
	public void testDeletePostStrategy() throws Exception {
		final Context ctx = new Context(this.is, this.db, HttpMethod.DELETE, "/users/testuser/posts/asdfsadf012312", new HashMap<Object, Object>(), null, null);
		assertTrue("failure initializing DeletePostStrategy", ctx.getStrategy() instanceof DeletePostStrategy);
	}
	
	@Test
	public void testGetListOfPostsStrategy() throws Exception {
		final Context c = new Context(this.is, db, HttpMethod.GET, "/posts", new HashMap<Object, Object>(), null, null);
		assertTrue("failure initializing GetListOfTagsStrategy", c.getStrategy() instanceof GetListOfPostsStrategy);
	}

	@Test
	public void testGetNewPostsStrategy() throws Exception {
		final Context c = new Context(this.is, db, HttpMethod.GET, "/posts/added", new HashMap<Object, Object>(), null, null);
		assertTrue("failure initializing GetNewPostsStrategy", c.getStrategy() instanceof GetNewPostsStrategy);
	}

	@Test
	public void testGetPopularPostsStrategy() throws Exception {
		final Context c = new Context(this.is, db, HttpMethod.GET, "/posts/popular", new HashMap<Object, Object>(), null, null);
		assertTrue("failure initializing GetPopularPostsStrategy", c.getStrategy() instanceof GetPopularPostsStrategy);
	}
	
	@Test
	public void testGetStandardPostStrategy() throws Exception {
		final Context c = new Context(this.is, db, HttpMethod.GET, "/posts/standard/hashhashhash", new HashMap<Object, Object>(), null, null);
		assertTrue("failure initializing GetPostDetailsStrategy for standard post", c.getStrategy() instanceof GetPostDetailsStrategy);
	}
	
	@Test
	public void testUpdateStandardPostStrategy() throws Exception {
		final Context c = new Context(this.is, db, HttpMethod.PUT, "/posts/standard/hashhashhash", new HashMap<Object, Object>(), null, null);
		assertTrue("failure initializing PutStandardPostStrategy", c.getStrategy() instanceof PutStandardPostStrategy);
	}
	
	@Test
	public void testDeleteStandardPostStrategy() throws Exception {
		final Context c = new Context(this.is, db, HttpMethod.DELETE, "/posts/standard/hashhashhash", new HashMap<Object, Object>(), null, null);
		assertTrue("failure initializing DeleteStandardPostStrategy", c.getStrategy() instanceof DeleteStandardPostStrategy);
	}
	
	@Test
	public void testAddReferenecesStrategy() throws Exception {
		final Context c = new Context(this.is, db, HttpMethod.POST, "/posts/standard/hashhashhash/references", new HashMap<Object, Object>(), null, null);
		assertTrue("failure initializing PostReferencesStrategy", c.getStrategy() instanceof PostReferencesStrategy);
	}
	
	@Test
	public void testDeleteReferenecesStrategy() throws Exception {
		final Context c = new Context(this.is, db, HttpMethod.DELETE, "/posts/standard/hashhashhash/references", new HashMap<Object, Object>(), null, null);
		assertTrue("failure initializing DeleteReferenceStrategy", c.getStrategy() instanceof DeleteReferencesStrategy);
	}

	@Test
	public void testWrongUsage() {
		try {
			new Context(this.is, null, HttpMethod.GET, null, Collections.EMPTY_MAP, null, null);
			fail("Should throw exception");
		} catch (final ValidationException ex) {
		}

		try {
			new Context(this.is, null, HttpMethod.GET, "", null, null, null);
			fail("Should throw exception");
		} catch (final RuntimeException ex) {
		}
	}
}