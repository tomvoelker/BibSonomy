package org.bibsonomy.rest.strategy;

import java.util.HashMap;

import junit.framework.TestCase;

import org.bibsonomy.rest.LogicInterface;
import org.bibsonomy.rest.database.TestDatabase;
import org.bibsonomy.rest.enums.HttpMethod;
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
		Context c = new Context( db, HttpMethod.GET.toString(), "/tags", new HashMap() );
		assertTrue( "failure initializing GetListOfTagsStrategy",
				c.getStrategy() instanceof GetListOfTagsStrategy );
	}
	
	public void testGetTagDetailsStrategy() throws Exception
	{
		Context c = new Context( db, HttpMethod.GET.toString(), "/tags/wichtig", new HashMap() );
		assertTrue( "failure initializing GetTagDetailsStrategy",
				c.getStrategy() instanceof GetTagDetailsStrategy );
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
 * Revision 1.2  2006/05/22 10:52:45  mbork
 * implemented context chooser for /posts
 *
 * Revision 1.1  2006/05/22 10:42:25  mbork
 * implemented context chooser for /tags
 *
 */