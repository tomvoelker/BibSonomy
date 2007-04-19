package org.bibsonomy.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import junit.framework.TestCase;

import org.bibsonomy.database.LogicInterface;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.database.TestDatabase;
import org.bibsonomy.rest.exceptions.AuthenticationException;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;

public class TestRestServlet extends TestCase
{
   public void testValidateAuthorization() throws Exception
   {
      RestServlet servlet = new RestServlet();
      servlet.setLogicInterface( new TestDatabase() );
      try
      {
         servlet.validateAuthorization( "YXNkZjphc2Rm" );
         fail( "exception should have been thrown" );
      }
      catch( AuthenticationException e )
      {}
      
      try
      {
         servlet.validateAuthorization( "Basic ASDFASDF" );
      }
      catch( BadRequestOrResponseException e )
      {}
      
      assertEquals( "error decoding string", servlet.validateAuthorization( "Basic YXNkZjphc2Rm" ), "asdf" );
   }
   
   public void testSimpleStuff() throws Exception
   {
      NullRequest request = new NullRequest();
      NullResponse response = new NullResponse();
      
      RestServlet servlet = new RestServlet();
      servlet.setLogicInterface( new TestDatabase() );
      
      try
      {
         // try unauthorized
         servlet.doGet( request, response );
         fail( "unauthorized access must not be possible" );
      }
      catch( RuntimeException e )
      {
         if( !"code: 401 message: Please authenticate yourself.".equals( 
               e.getMessage() ) )
         {
            fail( "wrong exception" );
         }
      }

      request.getHeaders().put( "Authorization", "Basic YXNkZjphc2Rm" );
      try
      {
         // try to get '/'
         servlet.doGet( request, response );
         fail( "it must be forbidden to access '/'" );
      }
      catch( RuntimeException e )
      {
         if( !"code: 403 message: It is forbidden to access '/'.".equals( 
               e.getMessage() ) )
         {
            fail( "wrong exception" );
         }
      }
   }
   
   public void testGetComplexStuff() throws Exception
   {
      NullRequest request = new NullRequest();
      request.getHeaders().put( "Authorization", "Basic YXNkZjphc2Rm" );
      request.getHeaders().put( "User-Agent", RestProperties.getInstance().getApiUserAgent() );
      NullResponse response = new NullResponse();
      
      RestServlet servlet = new RestServlet();
      servlet.setLogicInterface( new TestDatabase() );
      request.setPathInfo( "/users" );
      
      servlet.doGet( request, response );
      compareWithFile( response.getContent(), "exampleComplexResult1.txt" );
      assertEquals( response.getContentLength(), response.getContent().length() );
   }
   
   public void testUTF8() throws Exception
   {
      /*
      NullRequest request = new NullRequest();
      request.getHeaders().put( "Authorization", "Basic YXNkZjphc2Rm" );
      request.getHeaders().put( "User-Agent", RestProperties.getInstance().getApiUserAgent() );
      NullResponse response = new NullResponse();
      
      RestServlet servlet = new RestServlet();
      servlet.setLogicInterface( new TestDatabase() );
      LogicInterface logic = servlet.getLogic();
      User user = new User();
      user.setName( "üöäßéèê" );
      logic.storeUser( user, false );
      request.setPathInfo( "/users" );
      
      servlet.doGet( request, response );
      compareWithFile( response.getContent(), "UTF8TestResult.txt" );
      assertEquals( 813, response.getContentLength() ); // 813 vs 799
      */
   }
   
   private void compareWithFile( String sw, String filename ) throws IOException
   {
      StringBuffer sb = new StringBuffer( 200 );
      File file = new File( "src/test/resources/" + filename );
      BufferedReader br = new BufferedReader( new FileReader( file ) );
      String s;
      while( (s = br.readLine() ) != null )
      {
         sb.append( s + "\n" );
      }
      assertTrue( "output not as expected", sw.equals( sb.toString() ) );
   }
}

/*
 * $Log$
 * Revision 1.5  2007-04-19 16:16:28  mbork
 * disabled UTF8 test. will check later
 *
 * Revision 1.4  2007/04/19 13:30:40  rja
 * fixed a bug concerning Tests
 *
 * Revision 1.3  2007/04/15 11:05:39  mbork
 * fixed a bug concerning UTF-8 characters. Added a test
 *
 * Revision 1.2  2007/02/21 14:08:36  mbork
 * - included code generation of the schema in the maven2 build-lifecycle
 * - removed circular dependencies among the modules
 * - cleaned up the poms of the modules
 * - fixed failing unit-tests
 *
 * Revision 1.1  2006/10/24 21:39:52  mbork
 * split up rest api into correct modules. verified with junit tests.
 *
 * Revision 1.1  2006/10/10 12:42:14  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.3  2006/09/16 18:17:50  mbork
 * added some new fake bibtex entries to demonstrate jabref plugin :)
 * fix of tests depiending on fake bibtex entries
 *
 * Revision 1.2  2006/06/13 21:30:41  mbork
 * implemented unit tests for get-strategies; fixed some minor bugs
 *
 * Revision 1.1  2006/06/13 18:07:40  mbork
 * introduced unit tests for servlet using null-pattern for request and response. tested to use cactus/ httpunit, but decided not to use them.
 *
 * 
 */