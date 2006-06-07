package org.bibsonomy.rest.client.queries.post;

import java.io.StringWriter;

import org.bibsonomy.model.User;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.client.queries.AbstractQuery;
import org.bibsonomy.rest.renderer.impl.XMLRenderer;

/**
 * Use this Class to add an user to an already existing group
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class AddUserToGroupQuery extends AbstractQuery
{
	private boolean executed = false;
	private String result;
	private User user;
	private String groupName;

	/**
	 * Adds an user to an already existing group <p/> an
	 * {@link IllegalArgumentException} is thrown, if no groupname is given.
	 * <p/>note that the user and the group must exist before this query can be
	 * performed
	 * 
	 * @param groupname
	 *            name of the group the user is to be added to. the group must
	 *            exist, else a {@link IllegalArgumentException} is thrown
	 * @param user
	 *            the user to be added
	 */
	public AddUserToGroupQuery( String groupName, User user )
	{
		if( groupName == null || groupName.length() == 0 ) throw new IllegalArgumentException( "no groupName given" );
		if( user == null ) throw new IllegalArgumentException( "no user specified" );
		if( user.getName() == null || user.getName().length() == 0 ) throw new IllegalArgumentException( "no username specified" );
		this.groupName = groupName;
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
		result = performPostRequest( API_URL + URL_GROUPS + "/" + groupName + "/" + URL_USERS, sw.toString() );
	}
}

/*
 * $Log$
 * Revision 1.1  2006-06-07 19:37:29  mbork
 * implemented post queries
 *
 */