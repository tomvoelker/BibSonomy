package org.bibsonomy.rest.client.worker.impl;

import java.io.IOException;
import java.util.logging.Level;

import org.apache.commons.httpclient.methods.HeadMethod;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.client.worker.HttpWorker;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class HeadWorker extends HttpWorker
{
   private int httpResult;
   
   public HeadWorker( String username, String apiKey )
   {
      super( username, apiKey );
   }
   
   public String perform( String url ) throws ErrorPerformingRequestException
   {
      LOGGER.log( Level.INFO, "HEAD: URL: " + url );
      
      HeadMethod head = new HeadMethod( url );
      head.addRequestHeader( HEADER_AUTHORIZATION, encodeForAuthorization() );
      head.setDoAuthentication( true );
      head.setFollowRedirects( true );
      
      try
      {
         httpResult = getHttpClient().executeMethod( head );
         LOGGER.log( Level.INFO, "Result: " + httpResult );
         return head.getStatusText();
      }
      catch( IOException e )
      {
         LOGGER.log( Level.SEVERE, e.getMessage(), e );
         throw new ErrorPerformingRequestException( e );
      }
      finally
      {
         head.releaseConnection();
      }
   }

   /* (non-Javadoc)
    * @see org.bibsonomy.rest.client.worker.HttpWorker#getHttpResult()
    */
   @Override
   public int getHttpResult()
   {
      return httpResult;
   }
}

/*
 * $Log$
 * Revision 1.2  2007-05-10 20:25:40  mbork
 * api key implemented
 *
 * Revision 1.1  2006/10/24 21:39:22  mbork
 * split up rest api into correct modules. verified with junit tests.
 *
 * Revision 1.1  2006/10/10 12:42:13  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.1  2006/06/23 20:50:09  mbork
 * clientlib:
 * - added head request
 * - fixed issues with enums using uppercase letters invoked with toString()
 * serverlib:
 * - fixed some issues
 *
 */