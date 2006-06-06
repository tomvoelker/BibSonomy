package org.bibsonomy.rest.client.queries.get;

import java.util.logging.Level;

import org.bibsonomy.model.User;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.client.queries.AbstractQuery;
import org.bibsonomy.rest.exceptions.InvalidXMLException;
import org.bibsonomy.rest.renderer.xml.BibsonomyXML;
import org.bibsonomy.rest.renderer.xml.ModelFactory;

/**
 * Use this Class to receive details about an user of bibsonomy
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class GetUserDetailsQuery extends AbstractQuery
{
	private String username;
	private BibsonomyXML bibsonomyXML;

	/**
	 * Gets details of a user
	 * 
	 * @param username name of the user
	 */
	public GetUserDetailsQuery( String username )
	{
		if( username == null || username.length() == 0 ) throw new IllegalArgumentException( "no username given" );
		this.username = username;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.client.queries.AbstractQuery#getResult()
	 */
	@Override
	public User getResult() throws ErrorPerformingRequestException
	{
		if( bibsonomyXML == null ) throw new IllegalStateException( "Execute the query first." );

		if( bibsonomyXML.getUser() != null )
		{
			try
			{
				return ModelFactory.getInstance().createUser( bibsonomyXML.getUser() );
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
		bibsonomyXML = performGetRequest( API_URL + URL_USERS + "/" + username );
	}
}

/*
 * $Log$
 * Revision 1.1  2006-06-06 22:20:54  mbork
 * started implementing client api
 *
 */