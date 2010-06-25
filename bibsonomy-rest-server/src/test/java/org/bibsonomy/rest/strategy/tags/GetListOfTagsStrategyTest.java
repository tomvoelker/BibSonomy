package org.bibsonomy.rest.strategy.tags;

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
public class GetListOfTagsStrategyTest extends AbstractContextTest {

	/**
	 * @throws Exception 
	 */
	@Test
	public void testGetListOfTagsStrategy() throws Exception {
		final Context ctx = new Context(this.is, this.db, HttpMethod.GET, "/tags", new HashMap<String, String>(), null, null);
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ctx.perform(baos);

		// just test length, because the detail rendering output is tested by
		// the renderer test
		assertEquals(1532, baos.toString().length());
		assertEquals("text/xml", ctx.getContentType("firefox"));
		assertEquals("bibsonomy/tags+XML", ctx.getContentType(RestProperties.getInstance().getApiUserAgent()));
	}
}