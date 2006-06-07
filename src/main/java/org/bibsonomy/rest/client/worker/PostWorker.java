package org.bibsonomy.rest.client.worker;

import java.io.IOException;
import java.util.logging.Level;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class PostWorker extends HttpWorker
{
	private int httpResult;

	public PostWorker( String username, String password )
	{
		super( username, password );
	}
	
	public String perform( String url, String requestBody ) throws ErrorPerformingRequestException
	{
		LOGGER.log( Level.INFO, "POST: URL: " + url );
		
		PostMethod post = new PostMethod( url );
		post.addRequestHeader( HEADER_AUTHORIZATION, encodeForAuthorization() );
		post.setDoAuthentication( true );
		post.setFollowRedirects( true );
		
		post.setRequestEntity( new StringRequestEntity( requestBody ) );
		
		try
		{
			httpResult = getHttpClient().executeMethod( post );
			LOGGER.log( Level.INFO, "Result: " + httpResult );
			return post.getStatusText();
		}
		catch( IOException e )
		{
			LOGGER.log( Level.SEVERE, e.getMessage(), e );
			throw new ErrorPerformingRequestException( e );
		}
		finally
		{
			post.releaseConnection();
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