package org.bibsonomy.rest.strategy.tags;

import java.util.HashMap;

import org.bibsonomy.rest.NullRequest;
import org.bibsonomy.rest.NullResponse;
import org.bibsonomy.rest.database.TestDatabase;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.strategy.Context;

import junit.framework.TestCase;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetListOfTagsStrategyTest extends TestCase
{
   public void testGetListOfTagsStrategy()
   {
      Context c = new Context( new TestDatabase(), HttpMethod.GET, "/tags", new HashMap<String,String>() );
      NullRequest request = new NullRequest();
      NullResponse response = new NullResponse();
      c.perform( request, response );
      // just test length, because the detail rendering output is tested by the
      // renderer test
      assertEquals(  883, response.getStringWriter().toString().length() );
      assertEquals( "text/xml", c.getContentType( "firefox" ) );
      assertEquals( "bibsonomy/tags+XML", c.getContentType( Context.API_USER_AGENT ) );
   }
}

/*
 * $Log$
 * Revision 1.2  2006-07-05 16:27:57  mbork
 * fixed issues with link to next list of resources
 *
 * Revision 1.1  2006/07/05 15:20:13  mbork
 * implemented missing strategies, little changes on datamodel --> alpha :)
 *
 */