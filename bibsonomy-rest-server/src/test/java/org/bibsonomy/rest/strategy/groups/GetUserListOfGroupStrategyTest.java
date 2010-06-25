package org.bibsonomy.rest.strategy.groups;

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
 * @version $Id$
 */
public class GetUserListOfGroupStrategyTest extends AbstractContextTest {

	/**
	 * @throws ResourceMovedException 
	 * @throws ResourceNotFoundException 
	 * @throws NoSuchResourceException 
	 * @throws InternServerException 
	 * 
	 */
	@Test
	public void testGetUserListOfGroupStrategy() throws InternServerException, NoSuchResourceException, ResourceNotFoundException, ResourceMovedException {
		final Context c = new Context(this.is, this.db, HttpMethod.GET, "/groups/public/users", new HashMap<String, String>(), null, null);
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		c.perform(baos);

		// just test length, because the detail rendering output is tested by
		// the renderer test
		assertEquals(122, baos.toString().length());
		assertEquals("text/xml", c.getContentType("firefox"));
		assertEquals("bibsonomy/users+XML", c.getContentType(RestProperties.getInstance().getApiUserAgent()));
	}
}