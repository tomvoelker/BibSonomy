package org.bibsonomy.rest.strategy;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.strategy.posts.GetListOfPostsStrategy;
import org.bibsonomy.rest.strategy.posts.GetNewPostsStrategy;
import org.bibsonomy.rest.strategy.posts.GetPopularPostsStrategy;
import org.junit.Test;

/**
 * Tests for correct strategy initialization if requesting something under
 * /posts
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class ContextPostTest extends AbstractContextTest {

	@Test
	public void testGetListOfTagsStrategy() throws Exception {
		Context c = new Context(this.is, db, HttpMethod.GET, "/posts", new HashMap());
		assertTrue("failure initializing GetListOfTagsStrategy", c.getStrategy() instanceof GetListOfPostsStrategy);
	}

	@Test
	public void testGetNewPostsStrategy() throws Exception {
		Context c = new Context(this.is, db, HttpMethod.GET, "/posts/added", new HashMap());
		assertTrue("failure initializing GetNewPostsStrategy", c.getStrategy() instanceof GetNewPostsStrategy);
	}

	@Test
	public void testGetPopularPostsStrategy() throws Exception {
		Context c = new Context(this.is, db, HttpMethod.GET, "/posts/popular", new HashMap());
		assertTrue("failure initializing GetPopularPostsStrategy", c.getStrategy() instanceof GetPopularPostsStrategy);
	}
}