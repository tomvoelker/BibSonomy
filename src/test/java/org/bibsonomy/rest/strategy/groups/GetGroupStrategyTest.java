package org.bibsonomy.rest.strategy.groups;

import java.util.HashMap;

import junit.framework.TestCase;

import org.bibsonomy.rest.NullRequest;
import org.bibsonomy.rest.NullResponse;
import org.bibsonomy.rest.database.TestDatabase;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetGroupStrategyTest extends TestCase
{
   public void testGetGroupStrategy()
   {
      Context c = new Context( new TestDatabase(), "get", "/groups/public", new HashMap() );
      NullRequest request = new NullRequest();
      NullResponse response = new NullResponse();
      c.perform( request, response );
      // just test length, because the detail rendering output is tested by the
      // renderer test
      assertEquals( 211, response.getStringWriter().toString().length() );
      assertEquals( "text/xml", c.getContentType( "firefox" ) );
      assertEquals( "bibsonomy/group+XML", c.getContentType( Context.API_USER_AGENT ) );
   }
}

/*
 * $Log$
 * Revision 1.1  2006-06-13 21:30:40  mbork
 * implemented unit tests for get-strategies; fixed some minor bugs
 *
 */