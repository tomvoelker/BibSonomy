package org.bibsonomy.rest.client.worker.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.logging.Level;

import org.apache.commons.httpclient.methods.GetMethod;
import org.bibsonomy.rest.client.ProgressCallback;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.client.worker.HttpWorker;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class GetWorker extends HttpWorker
{
	private int httpResult;
   private ProgressCallback callback;

	public GetWorker( String username, String password, ProgressCallback callback )
	{
		super( username, password );
      this.callback = callback;
	}
	
	public Reader perform( String url ) throws ErrorPerformingRequestException
	{
		LOGGER.log( Level.INFO, "GET: URL: " + url );
		
		GetMethod get = new GetMethod( url );
		get.addRequestHeader( HEADER_AUTHORIZATION, encodeForAuthorization() );
		get.setDoAuthentication( true );
		get.setFollowRedirects( true );
		
		try
		{
			httpResult = getHttpClient().executeMethod( get );
			LOGGER.log( Level.INFO, "Result: " + httpResult );
			if( get.getResponseBodyAsStream() != null )
			{
            return performDownload( get.getResponseBodyAsStream(), get.getResponseContentLength() );
			}
		}
		catch( IOException e )
		{
			LOGGER.log( Level.SEVERE, e.getMessage(), e );
			throw new ErrorPerformingRequestException( e );
		}
		finally
		{
			get.releaseConnection();
		}
		throw new ErrorPerformingRequestException( "No Answer." );
	}
   
	private Reader performDownload( InputStream responseBodyAsStream, long responseContentLength ) throws ErrorPerformingRequestException, IOException
   {
      if( responseContentLength > Integer.MAX_VALUE ) throw new ErrorPerformingRequestException( "The response is to long: " + responseContentLength );
      StringBuilder sb = new StringBuilder( (int) responseContentLength );
      BufferedReader br = new BufferedReader( new InputStreamReader( responseBodyAsStream ) );
      int bytesRead = 0;
      String line = null;
      while( ( line = br.readLine() ) != null )
      {
         bytesRead += line.length();
         callCallback( bytesRead, responseContentLength );
         sb.append( line );
      }
      return new StringReader( sb.toString() );
   }

   private void callCallback( int bytesRead, long responseContentLength )
   {
      if( callback != null && responseContentLength > 0 )
      {
         callback.setPercent( (int) ( bytesRead / responseContentLength ) );
      }
   }

   /**
    * @return Returns the httpResult.
    */
	public int getHttpResult()
	{
		return httpResult;
	}
}

/*
 * $Log$
 * Revision 1.1  2006-10-24 21:39:22  mbork
 * split up rest api into correct modules. verified with junit tests.
 *
 * Revision 1.1  2006/10/10 12:42:13  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.3  2006/09/24 21:26:21  mbork
 * enabled sending the content-lenght, so that clients now can register callback objects which show the download progress.
 *
 * Revision 1.2  2006/09/16 18:19:16  mbork
 * completed client side api: client api now supports multiple renderers (currently only an implementation for the xml-renderer exists).
 *
 * Revision 1.1  2006/06/08 07:55:23  mbork
 * moved classes for clearness
 *
 * Revision 1.2  2006/06/07 19:37:28  mbork
 * implemented post queries
 *
 * Revision 1.1  2006/06/06 22:20:54  mbork
 * started implementing client api
 *
 */