package org.bibsonomy.rest.strategy.groups;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.renderer.RenderingFormat;
import org.bibsonomy.rest.strategy.AbstractContextTest;
import org.bibsonomy.rest.strategy.Context;
import org.junit.Test;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetListOfGroupsStrategyTest extends AbstractContextTest {

	/**
	 * @throws Exception 
	 */
	@Test
	public void testGetListOfGroupsStrategy() throws Exception {
		final Context ctx = new Context(HttpMethod.GET, "/groups", RenderingFormat.XML, this.is, null, this.db, new HashMap<String, String>(), null);
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ctx.perform(baos);

		// just test length, because the detail rendering output is tested by
		// the renderer test
		assertEquals(280, baos.toString().length());
		assertEquals("text/xml", ctx.getContentType("firefox"));
		assertEquals("bibsonomy/groups+XML", ctx.getContentType(RestProperties.getInstance().getApiUserAgent()));
	}
}