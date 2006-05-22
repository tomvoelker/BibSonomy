package org.bibsonomy.rest.strategy;

import java.util.HashMap;

import junit.framework.TestCase;

import org.bibsonomy.database.DbInterface;
import org.bibsonomy.database.TestDatabase;
import org.bibsonomy.rest.strategy.users.DeletePostStrategy;
import org.bibsonomy.rest.strategy.users.DeleteUserStrategy;
import org.bibsonomy.rest.strategy.users.GetPostDetailsStrategy;
import org.bibsonomy.rest.strategy.users.GetUserListStrategy;
import org.bibsonomy.rest.strategy.users.GetUserPostsStrategy;
import org.bibsonomy.rest.strategy.users.GetUserStrategy;
import org.bibsonomy.rest.strategy.users.PostPostStrategy;
import org.bibsonomy.rest.strategy.users.PostUserStrategy;
import org.bibsonomy.rest.strategy.users.PutPostStrategy;
import org.bibsonomy.rest.strategy.users.PutUserStrategy;

/**
 * Tests for correct strategy initialization if requesting something under /users
 *  
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class ContextUserTest extends TestCase 
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
	
	public void testGetListOfUsersStrategy() throws Exception
	{
		Context c = new Context( db, Context.HTTP_GET, "/users", new HashMap() );
		assertTrue( "failure initializing GetUserListStrategy",
				c.getStrategy() instanceof GetUserListStrategy );
	}
	
	public void testPostUserStrategy() throws Exception
	{
		Context c = new Context( db, Context.HTTP_POST, "/users", new HashMap() );
		assertTrue( "failure initializing PostUserStrategy",
				c.getStrategy() instanceof PostUserStrategy );
	}
	
	public void testGetDetailsOfUserStrategy() throws Exception
	{
		Context c = new Context( db, Context.HTTP_GET, "/users/testuser", new HashMap() );
		assertTrue( "failure initializing GetUserStrategy",
				c.getStrategy() instanceof GetUserStrategy );
	}
	
	public void testPutDetailsOfUserStrategy() throws Exception
	{
		Context c = new Context( db, Context.HTTP_PUT, "/users/testuser", new HashMap() );
		assertTrue( "failure initializing PutUserStrategy",
				c.getStrategy() instanceof PutUserStrategy );
	}
	
	public void testDeleteUserStrategy() throws Exception
	{
		Context c = new Context( db, Context.HTTP_DELETE, "/users/testuser", new HashMap() );
		assertTrue( "failure initializing DeleteUserStrategy",
				c.getStrategy() instanceof DeleteUserStrategy );
	}
	
	public void testGetUserPostsStrategy() throws Exception
	{
		Context c = new Context( db, Context.HTTP_GET, "/users/testuser/posts", new HashMap() );
		assertTrue( "failure initializing GetUserPostsStrategy",
				c.getStrategy() instanceof GetUserPostsStrategy );
	}

	public void testPostPostStrategy() throws Exception
	{
		Context c = new Context( db, Context.HTTP_POST, "/users/testuser/posts", new HashMap() );
		assertTrue( "failure initializing PostPostStrategy",
				c.getStrategy() instanceof PostPostStrategy );
	}
	
	public void testGetPostDetailsStrategy() throws Exception
	{
		Context c = new Context( db, Context.HTTP_GET, "/users/testuser/posts/asdfsadf012312", 
				new HashMap() );
		assertTrue( "failure initializing GetPostDetailsStrategy",
				c.getStrategy() instanceof GetPostDetailsStrategy );
	}
	
	public void testPutPostStrategy() throws Exception
	{
		Context c = new Context( db, Context.HTTP_PUT, "/users/testuser/posts/asdfsadf012312", 
				new HashMap() );
		assertTrue( "failure initializing PutPostStrategy",
				c.getStrategy() instanceof PutPostStrategy );
	}
	
	public void testDeletePostStrategy() throws Exception
	{
		Context c = new Context( db, Context.HTTP_DELETE, "/users/testuser/posts/asdfsadf012312", 
				new HashMap() );
		assertTrue( "failure initializing DeletePostStrategy",
				c.getStrategy() instanceof DeletePostStrategy );
	}
}

/*
 * $Log$
 * Revision 1.2  2006-05-22 10:34:38  mbork
 * implemented context chooser for /groups
 *
 * Revision 1.1  2006/05/21 20:31:51  mbork
 * continued implementing context
 *
 * Revision 1.1  2006/05/19 21:01:09  mbork
 * started implementing rest api
 *
 */