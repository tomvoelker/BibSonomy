package org.bibsonomy.rest.client.worker.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import org.apache.commons.httpclient.methods.GetMethod;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.client.worker.HttpWorker;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class GetWorker extends HttpWorker
{
	private int httpResult;

	public GetWorker( String username, String password )
	{
		super( username, password );
	}
	
	public InputStream perform( String url ) throws ErrorPerformingRequestException
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
				return get.getResponseBodyAsStream();
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
 * Revision 1.2  2006-09-16 18:19:16  mbork
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