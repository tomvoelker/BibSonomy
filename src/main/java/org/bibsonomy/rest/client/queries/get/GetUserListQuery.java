package org.bibsonomy.rest.client.queries.get;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bibsonomy.model.User;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.client.queries.AbstractQuery;
import org.bibsonomy.rest.exceptions.InvalidXMLException;
import org.bibsonomy.rest.renderer.xml.BibsonomyXML;
import org.bibsonomy.rest.renderer.xml.ModelFactory;
import org.bibsonomy.rest.renderer.xml.UserType;

/**
 * Use this Class to receive an ordered list of all users bibsonomy has
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class GetUserListQuery extends AbstractQuery
{
	private int start;
	private int end;
	private BibsonomyXML bibsonomyXML;

	/**
	 * Gets bibsonomy's user list
	 */
	public GetUserListQuery()
	{
		this( 0, 19 );
	}

	/**
	 * Gets bibsonomy's user list
	 * 
	 * @param start start of the list
	 * @param end end of the list
	 */
	public GetUserListQuery( int start, int end )
	{
		if( start < 0 ) throw new IllegalArgumentException( "start must be >= 0" );
		if( end < start ) throw new IllegalArgumentException( "end must be >= 0 and >= start value" );
		
		this.start = start;
		this.end = end;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.client.queries.AbstractQuery#getResult()
	 */
	@Override
	public List<User> getResult()
	{
		if( bibsonomyXML == null ) throw new IllegalStateException( "Execute the query first." );
		
		List<User> users = new ArrayList<User>();

		if( bibsonomyXML.getUsers() != null && bibsonomyXML.getUsers().getUser() != null )
		{
			for( UserType xmlUser: bibsonomyXML.getUsers().getUser() )
			{
				try
				{
					users.add( ModelFactory.getInstance().createUser( xmlUser ) );
				}
				catch( InvalidXMLException e )
				{
					LOGGER.log( Level.WARNING, e.getMessage(), e );
				}
			}
		}
		return users;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.client.queries.AbstractQuery#doExecute()
	 */
	@Override
	protected void doExecute() throws ErrorPerformingRequestException
	{
		bibsonomyXML = performGetRequest( API_URL + URL_USERS + "?start=" + start + "&end=" + end );
	}
}

/*
 * $Log$
 * Revision 1.1  2006-06-06 22:20:54  mbork
 * started implementing client api
 *
 */