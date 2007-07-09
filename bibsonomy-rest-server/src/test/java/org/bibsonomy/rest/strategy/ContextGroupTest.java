package org.bibsonomy.rest.strategy;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.strategy.groups.AddGroupStrategy;
import org.bibsonomy.rest.strategy.groups.AddUserToGroupStrategy;
import org.bibsonomy.rest.strategy.groups.DeleteGroupStrategy;
import org.bibsonomy.rest.strategy.groups.GetGroupStrategy;
import org.bibsonomy.rest.strategy.groups.GetListOfGroupsStrategy;
import org.bibsonomy.rest.strategy.groups.GetUserListOfGroupStrategy;
import org.bibsonomy.rest.strategy.groups.RemoveUserFromGroupStrategy;
import org.bibsonomy.rest.strategy.groups.UpdateGroupDetailsStrategy;
import org.junit.Test;

/**
 * Tests for correct strategy initialization if requesting something under
 * /groups
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class ContextGroupTest extends AbstractContextTest {

	@Test
	public void testGetListOfGroupsStrategy() throws Exception {
		Context c = new Context(this.is, db, HttpMethod.GET, "/groups", new HashMap());
		assertTrue("failure initializing GetListOfGroupsStrategy", c.getStrategy() instanceof GetListOfGroupsStrategy);
	}

	@Test
	public void testAddGroupStrategy() throws Exception {
		Context c = new Context(this.is, db, HttpMethod.POST, "/groups", new HashMap());
		assertTrue("failure initializing AddGroupStrategy", c.getStrategy() instanceof AddGroupStrategy);
	}

	@Test
	public void testGetDetailsOfGroupStrategy() throws Exception {
		Context c = new Context(this.is, db, HttpMethod.GET, "/groups/testgroup", new HashMap());
		assertTrue("failure initializing GetGroupStrategy", c.getStrategy() instanceof GetGroupStrategy);
	}

	@Test
	public void testUpdateGroupDetailsStrategy() throws Exception {
		Context c = new Context(this.is, db, HttpMethod.PUT, "/groups/testgroup", new HashMap());
		assertTrue("failure initializing UpdateGroupDetailsStrategy", c.getStrategy() instanceof UpdateGroupDetailsStrategy);
	}

	@Test
	public void testDeleteGroupStrategy() throws Exception {
		Context c = new Context(this.is, db, HttpMethod.DELETE, "/groups/testgroup", new HashMap());
		assertTrue("failure initializing DeleteGroupStrategy", c.getStrategy() instanceof DeleteGroupStrategy);
	}

	@Test
	public void testGetUserListOfGroupStrategy() throws Exception {
		Context c = new Context(this.is, db, HttpMethod.GET, "/groups/testgroup/users", new HashMap());
		assertTrue("failure initializing GetUserListOfGroupStrategy", c.getStrategy() instanceof GetUserListOfGroupStrategy);
	}

	@Test
	public void testAddUserToGroupStrategy() throws Exception {
		Context c = new Context(this.is, db, HttpMethod.POST, "/groups/testgroup/users", new HashMap());
		assertTrue("failure initializing AddUserToGroupStrategy", c.getStrategy() instanceof AddUserToGroupStrategy);
	}

	@Test
	public void testRemoveUserFromGroupStrategy() throws Exception {
		Context c = new Context(this.is, db, HttpMethod.DELETE, "/groups/testgroup/users/testuser", new HashMap());
		assertTrue("failure initializing RemoveUserFromGroupStrategy", c.getStrategy() instanceof RemoveUserFromGroupStrategy);
	}
}