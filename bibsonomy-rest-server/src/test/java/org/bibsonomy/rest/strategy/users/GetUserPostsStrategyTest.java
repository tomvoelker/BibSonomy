package org.bibsonomy.rest.strategy.users;

import java.io.StringWriter;
import java.util.HashMap;

import junit.framework.TestCase;

import org.bibsonomy.rest.NullRequest;
import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.database.TestDatabase;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetUserPostsStrategyTest extends TestCase
{
   public void testGetUserPostsStrategy()
   {
      Context c = new Context( new TestDatabase(), HttpMethod.GET, "/users/mbork/posts", new HashMap<String,String>() );
      NullRequest request = new NullRequest();
      StringWriter sw = new StringWriter();
      c.perform( request, sw );
      // just test length, because the detail rendering output is tested by the
      // renderer test
      assertEquals( "text/xml", c.getContentType( "firefox" ) );
      assertEquals( "bibsonomy/posts+XML", c.getContentType( RestProperties.getInstance().getApiUserAgent() ) );
   }
}

/*
 * $Log$
 * Revision 1.2  2007-02-16 16:12:41  mbork
 * fixed tests broken by the updates
 * added a test testing quotation of the urls in the xml
 *
 * Revision 1.1  2006/10/24 21:39:52  mbork
 * split up rest api into correct modules. verified with junit tests.
 *
 * Revision 1.1  2006/10/10 12:42:13  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.5  2006/09/24 21:26:21  mbork
 * enabled sending the content-lenght, so that clients now can register callback objects which show the download progress.
 *
 * Revision 1.4  2006/09/16 18:17:51  mbork
 * added some new fake bibtex entries to demonstrate jabref plugin :)
 * fix of tests depiending on fake bibtex entries
 *
 * Revision 1.3  2006/07/05 16:27:58  mbork
 * fixed issues with link to next list of resources
 *
 * Revision 1.2  2006/06/28 15:36:13  mbork
 * started implementing other http methods
 *
 * Revision 1.1  2006/06/13 21:30:40  mbork
 * implemented unit tests for get-strategies; fixed some minor bugs
 *
 */