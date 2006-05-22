package org.bibsonomy.rest.strategy;

import java.util.HashMap;

import junit.framework.TestCase;

import org.bibsonomy.database.DbInterface;
import org.bibsonomy.database.TestDatabase;
import org.bibsonomy.rest.strategy.groups.AddGroupStrategy;
import org.bibsonomy.rest.strategy.groups.AddUserToGroupStrategy;
import org.bibsonomy.rest.strategy.groups.DeleteGroupStrategy;
import org.bibsonomy.rest.strategy.groups.GetDetailsOfGroupStrategy;
import org.bibsonomy.rest.strategy.groups.GetListOfGroupsStrategy;
import org.bibsonomy.rest.strategy.groups.GetUserListOfGroupStrategy;
import org.bibsonomy.rest.strategy.groups.RemoveUserFromGroupStrategy;
import org.bibsonomy.rest.strategy.groups.UpdateGroupDetailsStrategy;

/**
 * Tests for correct strategy initialization if requesting something under /users
 *  
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class ContextGroupTest extends TestCase 
{
	private DbInterface db;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		this.db = new TestDatabase();
	}
	
	public void testGetListOfGroupsStrategy() throws Exception
	{
		Context c = new Context( db, Context.HTTP_GET, "/groups", new HashMap() );
		assertTrue( "failure initializing GetListOfGroupsStrategy",
				c.getStrategy() instanceof GetListOfGroupsStrategy );
	}
	
	public void testAddGroupStrategy() throws Exception
	{
		Context c = new Context( db, Context.HTTP_POST, "/groups", new HashMap() );
		assertTrue( "failure initializing AddGroupStrategy",
				c.getStrategy() instanceof AddGroupStrategy );
	}
	
	public void testGetDetailsOfGroupStrategy() throws Exception
	{
		Context c = new Context( db, Context.HTTP_GET, "/groups/testgroup", new HashMap() );
		assertTrue( "failure initializing GetDetailsOfGroupStrategy",
				c.getStrategy() instanceof GetDetailsOfGroupStrategy );
	}
	
	public void testUpdateGroupDetailsStrategy() throws Exception
	{
		Context c = new Context( db, Context.HTTP_PUT, "/groups/testgroup", new HashMap() );
		assertTrue( "failure initializing UpdateGroupDetailsStrategy",
				c.getStrategy() instanceof UpdateGroupDetailsStrategy );
	}
	
	public void testDeleteGroupStrategy() throws Exception
	{
		Context c = new Context( db, Context.HTTP_DELETE, "/groups/testgroup", new HashMap() );
		assertTrue( "failure initializing DeleteGroupStrategy",
				c.getStrategy() instanceof DeleteGroupStrategy );
	}
	
	public void testGetUserListOfGroupStrategy() throws Exception
	{
		Context c = new Context( db, Context.HTTP_GET, "/groups/testgroup/users", new HashMap() );
		assertTrue( "failure initializing GetUserListOfGroupStrategy",
				c.getStrategy() instanceof GetUserListOfGroupStrategy );
	}

	public void testAddUserToGroupStrategy() throws Exception
	{
		Context c = new Context( db, Context.HTTP_POST, "/groups/testgroup/users", new HashMap() );
		assertTrue( "failure initializing AddUserToGroupStrategy",
				c.getStrategy() instanceof AddUserToGroupStrategy );
	}
	
	public void testRemoveUserFromGroupStrategy() throws Exception
	{
		Context c = new Context( db, Context.HTTP_DELETE, "/groups/testgroup/users/testuser", 
				new HashMap() );
		assertTrue( "failure initializing RemoveUserFromGroupStrategy",
				c.getStrategy() instanceof RemoveUserFromGroupStrategy );
	}
}

/*
 * $Log$
 * Revision 1.1  2006-05-22 10:34:38  mbork
 * implemented context chooser for /groups
 *
 */