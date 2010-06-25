package org.bibsonomy.rest.strategy.users;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
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
public class GetUserPostsStrategyTest extends AbstractContextTest {

	/**
	 * @throws Exception 
	 */
	@Test
	public void testGetUserPostsStrategy() throws Exception {
		final Context ctx = new Context(this.is, this.db, HttpMethod.GET, "/users/mbork/posts", new HashMap<String, String>(), null, null);
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ctx.perform(baos);

		// just test length, because the detail rendering output is tested by
		// the renderer test
		assertEquals("text/xml", ctx.getContentType("firefox"));
		assertEquals("bibsonomy/posts+XML", ctx.getContentType(RestProperties.getInstance().getApiUserAgent()));
	}
}