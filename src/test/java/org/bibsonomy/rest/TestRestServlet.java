package org.bibsonomy.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.bibsonomy.rest.exceptions.AuthenticationException;
import org.bibsonomy.rest.exceptions.BadRequestException;
import org.bibsonomy.rest.strategy.Context;

public class TestRestServlet extends TestCase
{
   public void testValidateAuthorization() throws Exception
   {
      RestServlet servlet = new RestServlet();
      servlet.initTestScenario();
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
      catch( BadRequestException e )
      {}
      
      assertEquals( "error decoding string", servlet.validateAuthorization( "Basic YXNkZjphc2Rm" ), "asdf" );
   }
   
   public void testSimpleStuff() throws Exception
   {
      NullRequest request = new NullRequest();
      NullResponse response = new NullResponse();
      
      RestServlet servlet = new RestServlet();
      servlet.initTestScenario();
      
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
      request.getHeaders().put( "User-Agent", Context.API_USER_AGENT );
      NullResponse response = new NullResponse();
      
      RestServlet servlet = new RestServlet();
      servlet.initTestScenario();
      request.setPathInfo( "/users" );
      
      servlet.doGet( request, response );
      compareWithFile( response.getStringWriter(), "exampleComplexResult1.txt" );
   }
   
   private void compareWithFile( StringWriter sw, String filename ) throws IOException
   {
      StringBuffer sb = new StringBuffer( 200 );
      File file = new File( "src/test/java/org/bibsonomy/rest/" + filename );
      BufferedReader br = new BufferedReader( new FileReader( file ) );
      String s;
      while( (s = br.readLine() ) != null )
      {
         sb.append( s + "\n" );
      }
      assertTrue( "output not as expected", sw.toString().equals( sb.toString() + "\n" ) );
   }
}


/*
 * $Log$
 * Revision 1.2  2006-06-13 21:30:41  mbork
 * implemented unit tests for get-strategies; fixed some minor bugs
 *
 * Revision 1.1  2006/06/13 18:07:40  mbork
 * introduced unit tests for servlet using null-pattern for request and response. tested to use cactus/ httpunit, but decided not to use them.
 *
 * 
 */