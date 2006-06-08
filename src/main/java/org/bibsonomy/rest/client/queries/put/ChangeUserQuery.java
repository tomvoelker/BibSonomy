package org.bibsonomy.rest.client.queries.put;

import java.io.StringWriter;

import org.bibsonomy.model.User;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.client.queries.AbstractQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.renderer.impl.XMLRenderer;

/**
 * Use this Class to change details of an existing user account
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class ChangeUserQuery extends AbstractQuery
{
	private boolean executed = false;
	private String result;
	private User user;
	private String userName;

	/**
	 * Changes details of an existing user account <p/> both username of the
	 * existing user and username as parameter for the uri must be specified,
	 * else an {@link IllegalArgumentException} is thrown.
	 * 
	 * @param username
	 *            the user to change
	 * @param user
	 *            new values
	 */
	public ChangeUserQuery( String userName, User user )
	{
		if( userName == null || userName.length() == 0 ) throw new IllegalArgumentException( "no username given" );
		if( user == null ) throw new IllegalArgumentException( "no user specified" );
		if( user.getName() == null || user.getName().length() == 0 ) throw new IllegalArgumentException( "no username specified" );
		this.userName = userName;
		this.user = user;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.client.queries.AbstractQuery#getResult()
	 */
	@Override
	public String getResult()
	{
		if( !executed) throw new IllegalStateException( "Execute the query first." );
		return result;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.client.queries.AbstractQuery#doExecute()
	 */
	@Override
	protected void doExecute() throws ErrorPerformingRequestException
	{
		executed = true;
		StringWriter sw = new StringWriter( 100 );
		XMLRenderer.getInstance().serializeUser( sw, user, null );
		result = performRequest( HttpMethod.PUT, API_URL + URL_USERS + "/" + userName, sw.toString() );
	}
}

/*
 * $Log$
 * Revision 1.1  2006-06-08 07:41:11  mbork
 * client api completed
 *
 */