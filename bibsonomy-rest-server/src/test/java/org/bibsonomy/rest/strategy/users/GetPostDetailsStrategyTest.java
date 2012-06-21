package org.bibsonomy.rest.strategy.users;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.renderer.RendererFactory;
import org.bibsonomy.rest.renderer.RenderingFormat;
import org.bibsonomy.rest.strategy.AbstractContextTest;
import org.bibsonomy.rest.strategy.Context;
import org.junit.Test;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetPostDetailsStrategyTest extends AbstractContextTest {

	/**
	 * @throws Exception 
	 */
	@Test
	public void testGetPostDetailsStrategy() throws Exception {
		final Context ctx = new Context(HttpMethod.GET, "/api/users/mbork/posts/56c650d32e6f50d7f49f2613b4303ffc", RenderingFormat.XML, new RendererFactory(this.urlRenderer), this.is, null, this.db,
				new HashMap<String, String>(), null);
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ctx.perform(baos);

		// just test length, because the detail rendering output is tested by
		// the renderer test
		assertEquals(697, baos.toString().length());
		assertEquals("text/xml", ctx.getContentType("firefox"));
		assertEquals("bibsonomy/post+XML", ctx.getContentType(RESTConfig.API_USER_AGENT));
	}

	/**
	 * @throws Exception
	 */
	@Test(expected = NoSuchResourceException.class)
	public void testNotExistingPost() throws Exception {
		final Context ctx = new Context(HttpMethod.GET, "/users/mbork/posts/4444", RenderingFormat.XML, new RendererFactory(this.urlRenderer), this.is, null, this.db, new HashMap<String, String>(),
				null);
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ctx.perform(baos);
	}
}