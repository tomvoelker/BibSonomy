package org.bibsonomy.rest.client;

import junit.framework.TestCase;

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
         if( !e.getMessage().equals( "no username given" ) )
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
         if( !e.getMessage().equals( "no password given" ) )
         {
            fail( "wrong exception was thrown" );
         }
      }
      assertNotNull( "instantiation failed", new Bibsonomy( "user", "pw" ) );
   }
}

/*
 * $Log$
 * Revision 1.1  2006-06-08 16:14:36  mbork
 * Implemented some XMLRenderer functions, including unit-tests. introduced djunitplugin (see http://works.dgic.co.jp/djunit/index.html)
 *
 */