package org.bibsonomy.rest.strategy.users;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.common.exceptions.ResourceNotFoundException;
import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.strategy.AbstractContextTest;
import org.bibsonomy.rest.strategy.Context;
import org.junit.Test;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id: GetPostDetailsStrategyTest.java,v 1.3 2007/06/05 23:33:30
 *          cschenk Exp $
 */
public class GetPostDetailsStrategyTest extends AbstractContextTest {

	/**
	 * @throws ResourceMovedException 
	 * @throws ResourceNotFoundException 
	 * @throws NoSuchResourceException 
	 * @throws InternServerException 
	 * 
	 */
	@Test
	public void testGetPostDetailsStrategy() throws InternServerException, NoSuchResourceException, ResourceNotFoundException, ResourceMovedException {
		final Context ctx = new Context(this.is, this.db, HttpMethod.GET, "/users/mbork/posts/44444444444444444444444444444444", new HashMap<String, String>(), null, null);
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ctx.perform(baos);

		// just test length, because the detail rendering output is tested by
		// the renderer test
		assertEquals(697, baos.toString().length());
		assertEquals("text/xml", ctx.getContentType("firefox"));
		assertEquals("bibsonomy/post+XML", ctx.getContentType(RestProperties.getInstance().getApiUserAgent()));
	}
}