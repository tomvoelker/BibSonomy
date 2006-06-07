package org.bibsonomy.rest.client.queries.post;

import java.io.StringWriter;

import org.bibsonomy.model.User;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.client.queries.AbstractQuery;
import org.bibsonomy.rest.renderer.impl.XMLRenderer;

/**
 * Use this Class to create a new user account in bibsonomy
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class CreateUserQuery extends AbstractQuery
{
	private boolean executed = false;
	private String result;
	private User user;

	/**
	 * Creates a new user account in bibsonomy
	 * <p/>
	 * username and password must be specified, else an {@link IllegalArgumentException} is thrown.
	 * 
	 * @param user
	 *            the user to be created
	 */
	public CreateUserQuery( User user )
	{
		if( user == null ) throw new IllegalArgumentException( "no user specified" );
		if( user.getName() == null || user.getName().length() == 0 ) throw new IllegalArgumentException( "no username specified" );
		if( user.getPassword() == null || user.getPassword().length() == 0 ) throw new IllegalArgumentException( "no password specified" );
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
		result = performPostRequest( API_URL + URL_USERS, sw.toString() );
	}
}

/*
 * $Log$
 * Revision 1.1  2006-06-07 19:37:29  mbork
 * implemented post queries
 *
 */