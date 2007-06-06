package org.bibsonomy.rest.strategy;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.strategy.tags.GetListOfTagsStrategy;
import org.bibsonomy.rest.strategy.tags.GetTagDetailsStrategy;
import org.junit.Test;

/**
 * Tests for correct strategy initialization if requesting something under /tags
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class ContextTagTest extends AbstractContextTest {

	@Test
	public void testGetListOfTagsStrategy() throws Exception {
		Context c = new Context(db, HttpMethod.GET, "/tags", new HashMap());
		assertTrue("failure initializing GetListOfTagsStrategy", c.getStrategy() instanceof GetListOfTagsStrategy);
	}

	@Test
	public void testGetTagDetailsStrategy() throws Exception {
		Context c = new Context(db, HttpMethod.GET, "/tags/wichtig", new HashMap());
		assertTrue("failure initializing GetTagDetailsStrategy", c.getStrategy() instanceof GetTagDetailsStrategy);
	}
}