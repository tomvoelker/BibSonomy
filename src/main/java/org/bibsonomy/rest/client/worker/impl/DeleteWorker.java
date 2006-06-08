package org.bibsonomy.rest.client.worker.impl;

import java.io.IOException;
import java.util.logging.Level;

import org.apache.commons.httpclient.methods.DeleteMethod;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.client.worker.HttpWorker;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class DeleteWorker extends HttpWorker
{
	private int httpResult;

	public DeleteWorker( String username, String password )
	{
		super( username, password );
	}
	
	public String perform( String url ) throws ErrorPerformingRequestException
	{
		LOGGER.log( Level.INFO, "DELETE: URL: " + url );
		
		DeleteMethod delete = new DeleteMethod( url );
		delete.addRequestHeader( HEADER_AUTHORIZATION, encodeForAuthorization() );
		delete.setDoAuthentication( true );
		delete.setFollowRedirects( true );
		
		try
		{
			httpResult = getHttpClient().executeMethod( delete );
			LOGGER.log( Level.INFO, "Result: " + httpResult );
			return delete.getStatusText();
		}
		catch( IOException e )
		{
			LOGGER.log( Level.SEVERE, e.getMessage(), e );
			throw new ErrorPerformingRequestException( e );
		}
		finally
		{
			delete.releaseConnection();
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
 * Revision 1.1  2006-06-08 07:55:23  mbork
 * moved classes for clearness
 *
 * Revision 1.2  2006/06/07 19:37:28  mbork
 * implemented post queries
 *
 * Revision 1.1  2006/06/07 18:22:31  mbork
 * client api: finished implementing get and delete requests
 *
 */