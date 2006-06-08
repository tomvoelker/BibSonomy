package org.bibsonomy.rest.client.queries.delete;

import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.client.queries.AbstractQuery;
import org.bibsonomy.rest.enums.HttpMethod;

/**
 * Use this Class to delete a specified group
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class DeleteGroupQuery extends AbstractQuery
{
	private boolean executed = false;
	private String result;
	private String groupName;

	/**
	 * Deletes the specified group
	 * 
	 * @param groupName
	 *            the groupName of the group to be deleted
	 */
	public DeleteGroupQuery( String groupName )
	{
		if( groupName == null || groupName.length() == 0 ) throw new IllegalArgumentException( "no groupname given" );
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
		result = performRequest( HttpMethod.DELETE, API_URL + URL_GROUPS + "/" + groupName, null );
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