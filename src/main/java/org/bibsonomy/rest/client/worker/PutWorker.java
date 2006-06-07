package org.bibsonomy.rest.client.worker;

import java.io.IOException;
import java.util.logging.Level;

import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class PutWorker extends HttpWorker
{
	private int httpResult;

	public PutWorker( String username, String password )
	{
		super( username, password );
	}
	
	public String perform( String url, String requestBody ) throws ErrorPerformingRequestException
	{
		LOGGER.log( Level.INFO, "PUT: URL: " + url );
		
		PutMethod put = new PutMethod( url );
		put.addRequestHeader( HEADER_AUTHORIZATION, encodeForAuthorization() );
		put.setDoAuthentication( true );
		put.setFollowRedirects( true );
		
		put.setRequestEntity( new StringRequestEntity( requestBody ) );
		
		try
		{
			httpResult = getHttpClient().executeMethod( put );
			LOGGER.log( Level.INFO, "Result: " + httpResult );
			return put.getStatusText();
		}
		catch( IOException e )
		{
			LOGGER.log( Level.SEVERE, e.getMessage(), e );
			throw new ErrorPerformingRequestException( e );
		}
		finally
		{
			put.releaseConnection();
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
 * Revision 1.1  2006-06-07 19:37:28  mbork
 * implemented post queries
 *
 */