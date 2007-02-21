package org.bibsonomy.rest.strategy.groups;

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
public class GetGroupStrategyTest extends TestCase
{
   public void testGetGroupStrategy()
   {
      Context c = new Context( new TestDatabase(), HttpMethod.GET, "/groups/public", new HashMap() );
      NullRequest request = new NullRequest();
      StringWriter sw = new StringWriter();
      c.perform( request, sw );
      // just test length, because the detail rendering output is tested by the
      // renderer test
      assertEquals( 210, sw.toString().length() );
      assertEquals( "text/xml", c.getContentType( "firefox" ) );
      assertEquals( "bibsonomy/group+XML", c.getContentType( RestProperties.getInstance().getApiUserAgent() ) );
   }
}

/*
 * $Log$
 * Revision 1.2  2007-02-21 14:08:36  mbork
 * - included code generation of the schema in the maven2 build-lifecycle
 * - removed circular dependencies among the modules
 * - cleaned up the poms of the modules
 * - fixed failing unit-tests
 *
 * Revision 1.1  2006/10/24 21:39:52  mbork
 * split up rest api into correct modules. verified with junit tests.
 *
 * Revision 1.1  2006/10/10 12:42:15  cschenk
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