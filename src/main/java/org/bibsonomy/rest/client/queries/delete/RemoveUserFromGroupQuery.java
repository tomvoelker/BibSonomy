package org.bibsonomy.rest.client.queries.delete;

import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.client.queries.AbstractQuery;
import org.bibsonomy.rest.enums.HttpMethod;

/**
 * Use this Class to remove an user from a group
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class RemoveUserFromGroupQuery extends AbstractQuery
{
	private boolean executed = false;
	private String result;
	private String userName;
	private String groupName;

	/**
	 * Remove an user from a group
	 * 
	 * @param userName
	 *            the userName to be removed from the group
	 * @param groupName
	 *            group from which the user is to be removed
	 */
	public RemoveUserFromGroupQuery( String userName, String groupName )
	{
		if( userName == null || userName.length() == 0 ) throw new IllegalArgumentException( "no username given" );
		if( groupName == null || groupName.length() == 0 ) throw new IllegalArgumentException( "no groupname given" );
		this.userName = userName;
		this.groupName = groupName;
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
		result = performRequest( HttpMethod.DELETE, API_URL + URL_GROUPS + "/" + groupName+ "/" + URL_USERS + "/" + userName, null );
	}
}

/*
 * $Log$
 * Revision 1.3  2006-06-08 07:41:12  mbork
 * client api completed
 *
 * Revision 1.2  2006/06/07 18:25:13  mbork
 * forgot checking constructor parameters
 *
 * Revision 1.1  2006/06/07 18:22:31  mbork
 * client api: finished implementing get and delete requests
 *
 */