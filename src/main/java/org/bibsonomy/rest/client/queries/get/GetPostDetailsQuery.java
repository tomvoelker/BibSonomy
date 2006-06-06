package org.bibsonomy.rest.client.queries.get;

import java.util.logging.Level;

import org.bibsonomy.model.Post;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.client.queries.AbstractQuery;
import org.bibsonomy.rest.exceptions.InvalidXMLException;
import org.bibsonomy.rest.renderer.xml.BibsonomyXML;
import org.bibsonomy.rest.renderer.xml.ModelFactory;

/**
 * Use this Class to receive details about a post of an user
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class GetPostDetailsQuery extends AbstractQuery
{
	private String username;
	private String resourceHash;
	private BibsonomyXML bibsonomyXML;

	/**
	 * Gets details of a post of an user
	 * 
	 * @param username name of the user
	 * @param resourceHash hash of the resource
	 */
	public GetPostDetailsQuery( String username, String resourceHash )
	{
		if( username == null || username.length() == 0 ) throw new IllegalArgumentException( "no username given" );
		if( resourceHash == null || resourceHash.length() == 0 ) throw new IllegalArgumentException( "no resourceHash given" );
		this.username = username;
		this.resourceHash = resourceHash;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.client.queries.AbstractQuery#getResult()
	 */
	@Override
	public Post getResult() throws ErrorPerformingRequestException
	{
		if( bibsonomyXML == null ) throw new IllegalStateException( "Execute the query first." );

		if( bibsonomyXML.getPost() != null )
		{
			try
			{
				return ModelFactory.getInstance().createPost( bibsonomyXML.getPost() );
			}
			catch( InvalidXMLException e )
			{
				LOGGER.log( Level.WARNING, e.getMessage(), e );
				throw e;
			}
		}
		throw new ErrorPerformingRequestException( "The received document did not contain the requested data." );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bibsonomy.rest.client.queries.AbstractQuery#doExecute()
	 */
	@Override
	protected void doExecute() throws ErrorPerformingRequestException
	{
		bibsonomyXML = performGetRequest( API_URL + URL_USERS + "/" + username + "/" + URL_POSTS + "/" + resourceHash );
	}
}

/*
 * $Log$
 * Revision 1.1  2006-06-06 22:20:54  mbork
 * started implementing client api
 *
 */