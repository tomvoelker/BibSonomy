package org.bibsonomy.rest.strategy;

import java.util.HashMap;

import junit.framework.TestCase;

import org.bibsonomy.rest.LogicInterface;
import org.bibsonomy.rest.database.TestDatabase;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.strategy.posts.GetListOfPostsStrategy;
import org.bibsonomy.rest.strategy.posts.GetNewPostsStrategy;
import org.bibsonomy.rest.strategy.posts.GetPopularPostsStrategy;

/**
 * Tests for correct strategy initialization if requesting something under /posts
 *  
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class ContextPostTest extends TestCase 
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
	
	public void testGetListOfTagsStrategy() throws Exception
	{
		Context c = new Context( db, HttpMethod.GET, "/posts", new HashMap() );
		assertTrue( "failure initializing GetListOfTagsStrategy",
				c.getStrategy() instanceof GetListOfPostsStrategy );
	}
	
	public void testGetNewPostsStrategy() throws Exception
	{
		Context c = new Context( db, HttpMethod.GET, "/posts/added", new HashMap() );
		assertTrue( "failure initializing GetNewPostsStrategy",
				c.getStrategy() instanceof GetNewPostsStrategy );
	}
	
	public void testGetPopularPostsStrategy() throws Exception
	{
		Context c = new Context( db, HttpMethod.GET, "/posts/popular", new HashMap() );
		assertTrue( "failure initializing GetPopularPostsStrategy",
				c.getStrategy() instanceof GetPopularPostsStrategy );
	}
}

/*
 * $Log$
 * Revision 1.1  2006-10-24 21:39:52  mbork
 * split up rest api into correct modules. verified with junit tests.
 *
 * Revision 1.1  2006/10/10 12:42:15  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.4  2006/06/28 15:36:13  mbork
 * started implementing other http methods
 *
 * Revision 1.3  2006/05/24 20:05:55  jillig
 * TestDatabase verschoben
 *
 * Revision 1.2  2006/05/24 13:02:44  cschenk
 * Introduced an enum for the HttpMethod and moved the exceptions
 *
 * Revision 1.1  2006/05/22 10:52:45  mbork
 * implemented context chooser for /posts
 *
 */