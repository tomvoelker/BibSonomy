package org.bibsonomy.rest.strategy;

import java.util.HashMap;

import junit.framework.TestCase;

import org.bibsonomy.database.DbInterface;
import org.bibsonomy.database.TestDatabase;
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
	private DbInterface db;
	
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
		Context c = new Context( db, Context.HTTP_GET, "/posts", new HashMap() );
		assertTrue( "failure initializing GetListOfTagsStrategy",
				c.getStrategy() instanceof GetListOfPostsStrategy );
	}
	
	public void testGetNewPostsStrategy() throws Exception
	{
		Context c = new Context( db, Context.HTTP_GET, "/posts/added", new HashMap() );
		assertTrue( "failure initializing GetNewPostsStrategy",
				c.getStrategy() instanceof GetNewPostsStrategy );
	}
	
	public void testGetPopularPostsStrategy() throws Exception
	{
		Context c = new Context( db, Context.HTTP_GET, "/posts/popular", new HashMap() );
		assertTrue( "failure initializing GetPopularPostsStrategy",
				c.getStrategy() instanceof GetPopularPostsStrategy );
	}
}

/*
 * $Log$
 * Revision 1.1  2006-05-22 10:52:45  mbork
 * implemented context chooser for /posts
 *
 */