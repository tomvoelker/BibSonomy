package org.bibsonomy.rest.client;

import junit.framework.TestCase;

import org.bibsonomy.rest.client.queries.get.GetUserDetailsQuery;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class BibsonomyTest extends TestCase
{
   public void testInstantiation()
   {
      try
      {
         new Bibsonomy( "", "test" );
         fail( "exception should have been thrown" );
      }
      catch( IllegalArgumentException e )
      {
         if( !"The given username is not valid.".equals( e.getMessage() ) )
         {
            fail( "wrong exception was thrown" );
         }
      }
      try
      {
         new Bibsonomy( "test", "" );
         fail( "exception should have been thrown" );
      }
      catch( IllegalArgumentException e )
      {
         if( !"The given password is not valid.".equals( e.getMessage() ) )
         {
            fail( "wrong exception was thrown" );
         }
      }
      assertNotNull( "instantiation failed", new Bibsonomy( "user", "pw" ) );
   }
   
   public void testSetUsername()
   {
      Bibsonomy bib = new Bibsonomy();
      try
      {
         bib.setUsername( "" );
         fail( "exception should have been thrown" );
      }
      catch( IllegalArgumentException e )
      {
         if( !"The given username is not valid.".equals( e.getMessage() ) )
         {
            fail( "wrong exception was thrown" );
         }
      }
      bib.setUsername( "foo" );
   }
   
   public void testSetPassword()
   {
      Bibsonomy bib = new Bibsonomy();
      try
      {
         bib.setPassword( "" );
         fail( "exception should have been thrown" );
      }
      catch( IllegalArgumentException e )
      {
         if( !"The given password is not valid.".equals( e.getMessage() ) )
         {
            fail( "wrong exception was thrown" );
         }
      }
      bib.setPassword( "foo" );
   }
   
   public void testSetApiURL()
   {
      Bibsonomy bib = new Bibsonomy();
      try
      {
         bib.setApiURL( "" );
         fail( "exception should have been thrown" );
      }
      catch( IllegalArgumentException e )
      {
         if( !"The given apiURL is not valid.".equals( e.getMessage() ) )
         {
            fail( "wrong exception was thrown" );
         }
      }
      try
      {
         bib.setApiURL( "/" );
         fail( "exception should have been thrown" );
      }
      catch( IllegalArgumentException e )
      {
         if( !"The given apiURL is not valid.".equals( e.getMessage() ) )
         {
            fail( "wrong exception was thrown" );
         }
      }
      bib.setApiURL( "foo" );
   }
   
   public void testExecuteQuery() throws Exception
   {
      Bibsonomy bib = new Bibsonomy();
      try
      {
         bib.executeQuery( new GetUserDetailsQuery( "foo" ) );
         fail( "exception should have been thrown" );
      }
      catch( IllegalStateException e )
      {
      }
      bib.setUsername( "foo" );
      try
      {
         bib.executeQuery( new GetUserDetailsQuery( "foo" ) );
         fail( "exception should have been thrown" );
      }
      catch( IllegalStateException e )
      {
      }
   }
}

/*
 * $Log$
 * Revision 1.1  2006-10-10 12:42:15  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.3  2006/07/05 15:27:51  mbork
 * place constants on left side of comparison
 *
 * Revision 1.2  2006/06/14 18:23:21  mbork
 * refactored usage of username, password and host url
 *
 * Revision 1.1  2006/06/08 16:14:36  mbork
 * Implemented some XMLRenderer functions, including unit-tests. introduced djunitplugin (see http://works.dgic.co.jp/djunit/index.html)
 *
 */