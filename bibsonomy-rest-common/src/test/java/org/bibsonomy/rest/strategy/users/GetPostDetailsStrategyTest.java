package org.bibsonomy.rest.strategy.users;

import java.io.StringWriter;
import java.util.HashMap;

import junit.framework.TestCase;

import org.bibsonomy.rest.NullRequest;
import org.bibsonomy.rest.database.TestDatabase;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetPostDetailsStrategyTest extends TestCase
{
   public void testGetPostDetailsStrategy()
   {
      Context c = new Context( new TestDatabase(), HttpMethod.GET, "/users/mbork/posts/44444444444444444444444444444444", new HashMap<String,String>() );
      NullRequest request = new NullRequest();
      StringWriter sw = new StringWriter();
      c.perform( request, sw );
      // just test length, because the detail rendering output is tested by the
      // renderer test
      assertEquals(  637, sw.toString().length() );
      assertEquals( "text/xml", c.getContentType( "firefox" ) );
      assertEquals( "bibsonomy/post+XML", c.getContentType( Context.API_USER_AGENT ) );
   }
}

/*
 * $Log$
 * Revision 1.1  2006-10-10 12:42:13  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.3  2006/09/24 21:26:21  mbork
 * enabled sending the content-lenght, so that clients now can register callback objects which show the download progress.
 *
 * Revision 1.2  2006/06/28 15:36:13  mbork
 * started implementing other http methods
 *
 * Revision 1.1  2006/06/13 21:30:40  mbork
 * implemented unit tests for get-strategies; fixed some minor bugs
 *
 */