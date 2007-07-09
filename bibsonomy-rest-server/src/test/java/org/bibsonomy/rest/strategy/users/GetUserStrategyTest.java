package org.bibsonomy.rest.strategy.users;

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
public class GetUserStrategyTest extends AbstractContextTest {

	@Test
	public void testGetUserStrategy() {
		final Context ctx = new Context(this.is, this.db, HttpMethod.GET, "/users/mbork", new HashMap<String, String>());
		final StringWriter sw = new StringWriter();
		ctx.perform(sw);

		// just test length, because the detail rendering output is tested by
		// the renderer test
		assertEquals(255, sw.toString().length());
		assertEquals("text/xml", ctx.getContentType("firefox"));
		assertEquals("bibsonomy/user+XML", ctx.getContentType(RestProperties.getInstance().getApiUserAgent()));
	}
}