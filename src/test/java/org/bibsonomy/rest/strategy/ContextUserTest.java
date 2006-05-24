package org.bibsonomy.rest.strategy;

import java.util.HashMap;

import junit.framework.TestCase;

import org.bibsonomy.rest.LogicInterface;
import org.bibsonomy.rest.database.TestDatabase;
import org.bibsonomy.rest.enums.HttpMethod;
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
	private LogicInterface db;

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
		Context c = new Context( db, HttpMethod.GET.toString(), "/users", new HashMap() );
		assertTrue( "failure initializing GetUserListStrategy",
				c.getStrategy() instanceof GetUserListStrategy );
	}

	public void testPostUserStrategy() throws Exception
	{
		Context c = new Context( db, HttpMethod.POST.toString(), "/users", new HashMap() );
		assertTrue( "failure initializing PostUserStrategy",
				c.getStrategy() instanceof PostUserStrategy );
	}

	public void testGetDetailsOfUserStrategy() throws Exception
	{
		Context c = new Context( db, HttpMethod.GET.toString(), "/users/testuser", new HashMap() );
		assertTrue( "failure initializing GetUserStrategy",
				c.getStrategy() instanceof GetUserStrategy );
	}

	public void testPutDetailsOfUserStrategy() throws Exception
	{
		Context c = new Context( db, HttpMethod.PUT.toString(), "/users/testuser", new HashMap() );
		assertTrue( "failure initializing PutUserStrategy",
				c.getStrategy() instanceof PutUserStrategy );
	}

	public void testDeleteUserStrategy() throws Exception
	{
		Context c = new Context( db, HttpMethod.DELETE.toString(), "/users/testuser", new HashMap() );
		assertTrue( "failure initializing DeleteUserStrategy",
				c.getStrategy() instanceof DeleteUserStrategy );
	}

	public void testGetUserPostsStrategy() throws Exception
	{
		Context c = new Context( db, HttpMethod.GET.toString(), "/users/testuser/posts", new HashMap() );
		assertTrue( "failure initializing GetUserPostsStrategy",
				c.getStrategy() instanceof GetUserPostsStrategy );
	}

	public void testPostPostStrategy() throws Exception
	{
		Context c = new Context( db, HttpMethod.POST.toString(), "/users/testuser/posts", new HashMap() );
		assertTrue( "failure initializing PostPostStrategy",
				c.getStrategy() instanceof PostPostStrategy );
	}

	public void testGetPostDetailsStrategy() throws Exception
	{
		Context c = new Context( db, HttpMethod.GET.toString(), "/users/testuser/posts/asdfsadf012312", 
				new HashMap() );
		assertTrue( "failure initializing GetPostDetailsStrategy",
				c.getStrategy() instanceof GetPostDetailsStrategy );
	}

	public void testPutPostStrategy() throws Exception
	{
		Context c = new Context( db, HttpMethod.PUT.toString(), "/users/testuser/posts/asdfsadf012312", 
				new HashMap() );
		assertTrue( "failure initializing PutPostStrategy",
				c.getStrategy() instanceof PutPostStrategy );
	}

	public void testDeletePostStrategy() throws Exception
	{
		Context c = new Context( db, HttpMethod.DELETE.toString(), "/users/testuser/posts/asdfsadf012312", 
				new HashMap() );
		assertTrue( "failure initializing DeletePostStrategy",
				c.getStrategy() instanceof DeletePostStrategy );
	}
}

/*
 * $Log$
 * Revision 1.4  2006-05-24 20:05:55  jillig
 * TestDatabase verschoben
 *
 * Revision 1.3  2006/05/24 13:02:44  cschenk
 * Introduced an enum for the HttpMethod and moved the exceptions
 *
 * Revision 1.2  2006/05/22 10:34:38  mbork
 * implemented context chooser for /groups
 *
 * Revision 1.1  2006/05/21 20:31:51  mbork
 * continued implementing context
 *
 * Revision 1.1  2006/05/19 21:01:09  mbork
 * started implementing rest api
 *
 */