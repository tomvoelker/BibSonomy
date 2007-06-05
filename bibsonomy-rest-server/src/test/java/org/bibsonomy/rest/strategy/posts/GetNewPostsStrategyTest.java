package org.bibsonomy.rest.strategy.posts;

import java.io.StringWriter;
import java.util.HashMap;

import junit.framework.TestCase;

import org.bibsonomy.rest.NullRequest;
import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.database.TestDatabase;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetNewPostsStrategyTest extends TestCase {

	public void testGetNewPostsStrategy() {
		final Context c = new Context(new TestDatabase(), HttpMethod.GET, "/posts/added", new HashMap<String, String>());
		final NullRequest request = new NullRequest();
		final StringWriter sw = new StringWriter();
		c.perform(request, sw);

		// just test length, because the detail rendering output is tested by
		// the renderer test
		assertEquals(9461, sw.toString().length());
		assertEquals("text/xml", c.getContentType("firefox"));
		assertEquals("bibsonomy/posts+XML", c.getContentType(RestProperties.getInstance().getApiUserAgent()));
	}
}