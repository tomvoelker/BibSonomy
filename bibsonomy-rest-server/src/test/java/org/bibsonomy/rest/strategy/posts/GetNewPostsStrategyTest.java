package org.bibsonomy.rest.strategy.posts;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;
import java.util.HashMap;

import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.strategy.AbstractContextTest;
import org.bibsonomy.rest.strategy.Context;
import org.junit.Test;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetNewPostsStrategyTest extends AbstractContextTest {

	@Test
	public void testGetNewPostsStrategy() {
		final Context c = new Context(this.is, this.db, HttpMethod.GET, "/posts/added", new HashMap<String, String>());
		final StringWriter sw = new StringWriter();
		c.perform(sw);

		// just test length, because the detail rendering output is tested by
		// the renderer test
		assertEquals(10065, sw.toString().length());
		assertEquals("text/xml", c.getContentType("firefox"));
		assertEquals("bibsonomy/posts+XML", c.getContentType(RestProperties.getInstance().getApiUserAgent()));
	}
}