package org.bibsonomy.rest.strategy.groups;

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
public class GetListOfGroupsStrategyTest extends TestCase {

	public void testGetListOfGroupsStrategy() {
		final Context ctx = new Context(new TestDatabase(), HttpMethod.GET, "/groups", new HashMap<String, String>());
		final NullRequest request = new NullRequest();
		final StringWriter sw = new StringWriter();
		ctx.perform(request, sw);

		// just test length, because the detail rendering output is tested by
		// the renderer test
		assertEquals(259, sw.toString().length());
		assertEquals("text/xml", ctx.getContentType("firefox"));
		assertEquals("bibsonomy/groups+XML", ctx.getContentType(RestProperties.getInstance().getApiUserAgent()));
	}
}