package org.bibsonomy.rest.strategy.groups;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;
import java.util.HashMap;

import org.bibsonomy.rest.NullRequest;
import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.strategy.AbstractContextTest;
import org.bibsonomy.rest.strategy.Context;
import org.junit.Test;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetUserListOfGroupStrategyTest extends AbstractContextTest {

	@Test
	public void testGetUserListOfGroupStrategy() {
		final Context c = new Context(this.db, HttpMethod.GET, "/groups/public/users", new HashMap<String, String>());
		final NullRequest request = new NullRequest();
		final StringWriter sw = new StringWriter();
		c.perform(request, sw);

		// just test length, because the detail rendering output is tested by
		// the renderer test
		assertEquals(666, sw.toString().length());
		assertEquals("text/xml", c.getContentType("firefox"));
		assertEquals("bibsonomy/users+XML", c.getContentType(RestProperties.getInstance().getApiUserAgent()));
	}
}