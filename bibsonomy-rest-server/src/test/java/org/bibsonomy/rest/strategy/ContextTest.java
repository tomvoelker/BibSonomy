package org.bibsonomy.rest.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.renderer.RenderingFormat;
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
import org.bibsonomy.rest.strategy.posts.community.PutCommunityPostStrategy;
import org.bibsonomy.rest.strategy.posts.community.references.DeleteReferencesStrategy;
import org.bibsonomy.rest.strategy.posts.community.references.PostReferencesStrategy;
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
		
		final Context ctx = new Context(HttpMethod.GET, "/api/users/egal/posts", RenderingFormat.XML.XML, this.urlRenderer, this.is, null, this.db, this.parameterMap, null);

		final List<String> tags = ctx.getTags("tags");
		assertTrue(NOT_SPLITTED_MSG, tags.contains("foo"));
		assertTrue(NOT_SPLITTED_MSG, tags.contains("bar"));
		assertEquals(NOT_SPLITTED_MSG, 2, tags.size());
	}

	@Test
	public void testGetTags() {
		this.parameterMap.put("tags", new String[] { "foo bar ->subtags -->transitiveSubtags supertags-> transitiveSupertags--> <->correlated" });
		final Context ctx = new Context(HttpMethod.GET, "/api/users/egal/posts", RenderingFormat.XML.XML, this.urlRenderer, this.is, null, this.db, this.parameterMap, null);

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
		final Context c = new Context(HttpMethod.GET, "/api/groups", RenderingFormat.XML, this.urlRenderer, this.is, null, db, new HashMap<Object, Object>(), null);
		assertTrue("failure initializing GetListOfGroupsStrategy", c.getStrategy() instanceof GetListOfGroupsStrategy);
	}

	@Test
	public void testAddGroupStrategy() throws Exception {
		final Context c = new Context(HttpMethod.POST, "/api/groups", RenderingFormat.XML, this.urlRenderer, this.is, null, db, new HashMap<Object, Object>(), null);
		assertTrue("failure initializing AddGroupStrategy", c.getStrategy() instanceof AddGroupStrategy);
	}

	@Test
	public void testGetDetailsOfGroupStrategy() throws Exception {
		final Context c = new Context(HttpMethod.GET, "/api/groups/testgroup", RenderingFormat.XML, this.urlRenderer, this.is, null, db, new HashMap<Object, Object>(), null);
		assertTrue("failure initializing GetGroupStrategy", c.getStrategy() instanceof GetGroupStrategy);
	}

	@Test
	public void testUpdateGroupDetailsStrategy() throws Exception {
		final Context c = new Context(HttpMethod.PUT, "/api/groups/testgroup", RenderingFormat.XML, this.urlRenderer, this.is, null, db, new HashMap<Object, Object>(), null);
		assertTrue("failure initializing UpdateGroupDetailsStrategy", c.getStrategy() instanceof UpdateGroupDetailsStrategy);
	}

	@Test
	public void testDeleteGroupStrategy() throws Exception {
		final Context c = new Context(HttpMethod.DELETE, "/api/groups/testgroup", RenderingFormat.XML, this.urlRenderer, this.is, null, db, new HashMap<Object, Object>(), null);
		assertTrue("failure initializing DeleteGroupStrategy", c.getStrategy() instanceof DeleteGroupStrategy);
	}

	@Test
	public void testGetUserListOfGroupStrategy() throws Exception {
		final Context c = new Context(HttpMethod.GET, "/api/groups/testgroup/users", RenderingFormat.XML, this.urlRenderer, this.is, null, db, new HashMap<Object, Object>(), null);
		assertTrue("failure initializing GetUserListOfGroupStrategy", c.getStrategy() instanceof GetUserListOfGroupStrategy);
	}

	@Test
	public void testAddUserToGroupStrategy() throws Exception {
		final Context c = new Context(HttpMethod.POST, "/api/groups/testgroup/users", RenderingFormat.XML, this.urlRenderer, this.is, null, db, new HashMap<Object, Object>(), null);
		assertTrue("failure initializing AddUserToGroupStrategy", c.getStrategy() instanceof AddUserToGroupStrategy);
	}

	@Test
	public void testRemoveUserFromGroupStrategy() throws Exception {
		final Context c = new Context(HttpMethod.DELETE, "/api/groups/testgroup/users/testuser", RenderingFormat.XML, this.urlRenderer, this.is, null, db, new HashMap<Object, Object>(), null);
		assertTrue("failure initializing RemoveUserFromGroupStrategy", c.getStrategy() instanceof RemoveUserFromGroupStrategy);
	}
	
	@Test
	public void testGetListOfTagsStrategy() throws Exception {
		final Context c = new Context(HttpMethod.GET, "/api/tags", RenderingFormat.XML, this.urlRenderer, this.is, null, db, new HashMap<Object, Object>(), null);
		assertTrue("failure initializing GetListOfTagsStrategy", c.getStrategy() instanceof GetListOfTagsStrategy);
	}

	@Test
	public void testGetTagDetailsStrategy() throws Exception {
		final Context c = new Context(HttpMethod.GET, "/api/tags/wichtig", RenderingFormat.XML, this.urlRenderer, this.is, null, db, new HashMap<Object, Object>(), null);
		assertTrue("failure initializing GetTagDetailsStrategy", c.getStrategy() instanceof GetTagDetailsStrategy);
	}
	
	@Test
	public void testGetListOfUsersStrategy() throws Exception {
		final Context ctx = new Context(HttpMethod.GET, "/api/users", RenderingFormat.XML, this.urlRenderer, this.is, null, this.db, new HashMap<Object, Object>(), null);
		assertTrue("failure initializing GetUserListStrategy", ctx.getStrategy() instanceof GetUserListStrategy);
	}

	@Test
	public void testPostUserStrategy() throws Exception {
		final Context ctx = new Context(HttpMethod.POST, "/api/users", RenderingFormat.XML, this.urlRenderer, this.is, null, this.db, new HashMap<Object, Object>(), null);
		assertTrue("failure initializing PostUserStrategy", ctx.getStrategy() instanceof PostUserStrategy);
	}

	@Test
	public void testGetDetailsOfUserStrategy() throws Exception {
		final Context ctx = new Context(HttpMethod.GET, "/api/users/testuser", RenderingFormat.XML, this.urlRenderer, this.is, null, this.db, new HashMap<Object, Object>(), null);
		assertTrue("failure initializing GetUserStrategy", ctx.getStrategy() instanceof GetUserStrategy);
	}

	@Test
	public void testPutDetailsOfUserStrategy() throws Exception {
		final Context ctx = new Context(HttpMethod.PUT, "/api/users/testuser", RenderingFormat.XML, this.urlRenderer, this.is, null, this.db, new HashMap<Object, Object>(), null);
		assertTrue("failure initializing PutUserStrategy", ctx.getStrategy() instanceof PutUserStrategy);
	}

	@Test
	public void testDeleteUserStrategy() throws Exception {
		final Context ctx = new Context(HttpMethod.DELETE, "/api/users/testuser", RenderingFormat.XML, this.urlRenderer, this.is, null, this.db, new HashMap<Object, Object>(), null);
		assertTrue("failure initializing DeleteUserStrategy", ctx.getStrategy() instanceof DeleteUserStrategy);
	}

	@Test
	public void testGetUserPostsStrategy() throws Exception {
		final Context ctx = new Context(HttpMethod.GET, "/api/users/testuser/posts", RenderingFormat.XML, this.urlRenderer, this.is, null, this.db, new HashMap<Object, Object>(), null);
		assertTrue("failure initializing GetUserPostsStrategy", ctx.getStrategy() instanceof GetUserPostsStrategy);
	}

	@Test
	public void testPostPostStrategy() throws Exception {
		final Context ctx = new Context(HttpMethod.POST, "/api/users/testuser/posts", RenderingFormat.XML, this.urlRenderer, this.is, null, this.db, new HashMap<Object, Object>(), null);
		assertTrue("failure initializing PostPostStrategy", ctx.getStrategy() instanceof PostPostStrategy);
	}

	@Test
	public void testGetPostDetailsStrategy() throws Exception {
		final Context ctx = new Context(HttpMethod.GET, "/api/users/testuser/posts/asdfsadf012312", RenderingFormat.XML, this.urlRenderer, this.is, null, this.db, new HashMap<Object, Object>(), null);
		assertTrue("failure initializing GetPostDetailsStrategy", ctx.getStrategy() instanceof GetPostDetailsStrategy);
	}

	@Test
	public void testPutPostStrategy() throws Exception {
		final Context ctx = new Context(HttpMethod.PUT, "/api/users/testuser/posts/asdfsadf012312", RenderingFormat.XML, this.urlRenderer, this.is, null, this.db, new HashMap<Object, Object>(), null);
		assertTrue("failure initializing PutPostStrategy", ctx.getStrategy() instanceof PutPostStrategy);
	}

	@Test
	public void testDeletePostStrategy() throws Exception {
		final Context ctx = new Context(HttpMethod.DELETE, "/api/users/testuser/posts/asdfsadf012312", RenderingFormat.XML, this.urlRenderer, this.is, null, this.db, new HashMap<Object, Object>(), null);
		assertTrue("failure initializing DeletePostStrategy", ctx.getStrategy() instanceof DeletePostStrategy);
	}
	
	@Test
	public void testGetListOfPostsStrategy() throws Exception {
		final Context c = new Context(HttpMethod.GET, "/api/posts", RenderingFormat.XML, this.urlRenderer, this.is, null, db, new HashMap<Object, Object>(), null);
		assertTrue("failure initializing GetListOfTagsStrategy", c.getStrategy() instanceof GetListOfPostsStrategy);
	}

	@Test
	public void testGetNewPostsStrategy() throws Exception {
		final Context c = new Context(HttpMethod.GET, "/api/posts/added", RenderingFormat.XML, this.urlRenderer, this.is, null, db, new HashMap<Object, Object>(), null);
		assertTrue("failure initializing GetNewPostsStrategy", c.getStrategy() instanceof GetNewPostsStrategy);
	}

	@Test
	public void testGetPopularPostsStrategy() throws Exception {
		final Context c = new Context(HttpMethod.GET, "/api/posts/popular", RenderingFormat.XML, this.urlRenderer, this.is, null, db, new HashMap<Object, Object>(), null);
		assertTrue("failure initializing GetPopularPostsStrategy", c.getStrategy() instanceof GetPopularPostsStrategy);
	}
	
	@Test
	public void testGetCommunityPostStrategy() throws Exception {
		final Context c = new Context(HttpMethod.GET, "/api/posts/community/hashhashhash", RenderingFormat.XML, this.urlRenderer, this.is, null, db, new HashMap<Object, Object>(), null);
		assertTrue("failure initializing GetPostDetailsStrategy for standard post", c.getStrategy() instanceof GetPostDetailsStrategy);
	}
	
	@Test
	public void testUpdateCommunityPostStrategy() throws Exception {
		final Context c = new Context(HttpMethod.PUT, "/api/posts/community/hashhashhash", RenderingFormat.XML, this.urlRenderer, this.is, null, db, new HashMap<Object, Object>(), null);
		assertTrue("failure initializing PutStandardPostStrategy", c.getStrategy() instanceof PutCommunityPostStrategy);
	}
	
	@Test
	public void testDeleteCommunityPostStrategy() throws Exception {
		final Context c = new Context(HttpMethod.DELETE, "/api/posts/community/hashhashhash", RenderingFormat.XML, this.urlRenderer, this.is, null, db, new HashMap<Object, Object>(), null);
		assertTrue("failure initializing DeleteStandardPostStrategy", c.getStrategy() instanceof DeletePostStrategy);
	}
	
	@Test
	public void testAddReferenecesStrategy() throws Exception {
		final Context c = new Context(HttpMethod.POST, "/api/posts/community/hashhashhash/references", RenderingFormat.XML, this.urlRenderer, this.is, null, db, new HashMap<Object, Object>(), null);
		assertTrue("failure initializing PostReferencesStrategy", c.getStrategy() instanceof PostReferencesStrategy);
	}
	
	@Test
	public void testDeleteReferenecesStrategy() throws Exception {
		final Context c = new Context(HttpMethod.DELETE, "/api/posts/community/hashhashhash/references", RenderingFormat.XML, this.urlRenderer, this.is, null, db, new HashMap<Object, Object>(), null);
		assertTrue("failure initializing DeleteReferenceStrategy", c.getStrategy() instanceof DeleteReferencesStrategy);
	}

	@Test
	public void testWrongUsage() {
		try {
			new Context(HttpMethod.GET, null, RenderingFormat.XML, this.urlRenderer, this.is, null, null, Collections.EMPTY_MAP, null);
			fail("Should throw exception");
		} catch (final AccessDeniedException ex) {
		}

		try {
			new Context(HttpMethod.GET, "", RenderingFormat.XML, this.urlRenderer, this.is, null, null, null, null);
			fail("Should throw exception");
		} catch (final RuntimeException ex) {
		}
	}
}