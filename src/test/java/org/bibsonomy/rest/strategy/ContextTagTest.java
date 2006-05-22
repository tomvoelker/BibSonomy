package org.bibsonomy.rest.strategy;

import java.util.HashMap;

import junit.framework.TestCase;

import org.bibsonomy.database.DbInterface;
import org.bibsonomy.database.TestDatabase;
import org.bibsonomy.rest.strategy.tags.GetListOfTagsStrategy;
import org.bibsonomy.rest.strategy.tags.GetTagDetailsStrategy;

/**
 * Tests for correct strategy initialization if requesting something under /tags
 *  
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class ContextTagTest extends TestCase 
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
		Context c = new Context( db, Context.HTTP_GET, "/tags", new HashMap() );
		assertTrue( "failure initializing GetListOfTagsStrategy",
				c.getStrategy() instanceof GetListOfTagsStrategy );
	}
	
	public void testGetTagDetailsStrategy() throws Exception
	{
		Context c = new Context( db, Context.HTTP_GET, "/tags/wichtig", new HashMap() );
		assertTrue( "failure initializing GetTagDetailsStrategy",
				c.getStrategy() instanceof GetTagDetailsStrategy );
	}
}

/*
 * $Log$
 * Revision 1.2  2006-05-22 10:52:45  mbork
 * implemented context chooser for /posts
 *
 * Revision 1.1  2006/05/22 10:42:25  mbork
 * implemented context chooser for /tags
 *
 */