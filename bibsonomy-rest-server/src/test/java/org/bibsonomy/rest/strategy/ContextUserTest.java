package org.bibsonomy.rest.strategy;

import java.util.HashMap;

import org.bibsonomy.rest.enums.HttpMethod;
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

/**
 * Tests for correct strategy initialization if requesting something under
 * /users
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class ContextUserTest extends AbstractContextTest {

	public void testGetListOfUsersStrategy() throws Exception {
		final Context ctx = new Context(this.db, HttpMethod.GET, "/users", new HashMap());
		assertTrue("failure initializing GetUserListStrategy", ctx.getStrategy() instanceof GetUserListStrategy);
	}

	public void testPostUserStrategy() throws Exception {
		final Context ctx = new Context(this.db, HttpMethod.POST, "/users", new HashMap());
		assertTrue("failure initializing PostUserStrategy", ctx.getStrategy() instanceof PostUserStrategy);
	}

	public void testGetDetailsOfUserStrategy() throws Exception {
		final Context ctx = new Context(this.db, HttpMethod.GET, "/users/testuser", new HashMap());
		assertTrue("failure initializing GetUserStrategy", ctx.getStrategy() instanceof GetUserStrategy);
	}

	public void testPutDetailsOfUserStrategy() throws Exception {
		final Context ctx = new Context(this.db, HttpMethod.PUT, "/users/testuser", new HashMap());
		assertTrue("failure initializing PutUserStrategy", ctx.getStrategy() instanceof PutUserStrategy);
	}

	public void testDeleteUserStrategy() throws Exception {
		final Context ctx = new Context(this.db, HttpMethod.DELETE, "/users/testuser", new HashMap());
		assertTrue("failure initializing DeleteUserStrategy", ctx.getStrategy() instanceof DeleteUserStrategy);
	}

	public void testGetUserPostsStrategy() throws Exception {
		final Context ctx = new Context(this.db, HttpMethod.GET, "/users/testuser/posts", new HashMap());
		assertTrue("failure initializing GetUserPostsStrategy", ctx.getStrategy() instanceof GetUserPostsStrategy);
	}

	public void testPostPostStrategy() throws Exception {
		final Context ctx = new Context(this.db, HttpMethod.POST, "/users/testuser/posts", new HashMap());
		assertTrue("failure initializing PostPostStrategy", ctx.getStrategy() instanceof PostPostStrategy);
	}

	public void testGetPostDetailsStrategy() throws Exception {
		final Context ctx = new Context(this.db, HttpMethod.GET, "/users/testuser/posts/asdfsadf012312", new HashMap());
		assertTrue("failure initializing GetPostDetailsStrategy", ctx.getStrategy() instanceof GetPostDetailsStrategy);
	}

	public void testPutPostStrategy() throws Exception {
		final Context ctx = new Context(this.db, HttpMethod.PUT, "/users/testuser/posts/asdfsadf012312", new HashMap());
		assertTrue("failure initializing PutPostStrategy", ctx.getStrategy() instanceof PutPostStrategy);
	}

	public void testDeletePostStrategy() throws Exception {
		final Context ctx = new Context(this.db, HttpMethod.DELETE, "/users/testuser/posts/asdfsadf012312", new HashMap());
		assertTrue("failure initializing DeletePostStrategy", ctx.getStrategy() instanceof DeletePostStrategy);
	}
}