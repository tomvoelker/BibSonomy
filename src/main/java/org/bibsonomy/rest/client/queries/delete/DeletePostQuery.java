package org.bibsonomy.rest.client.queries.delete;

import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.enums.HttpMethod;

/**
 * Use this Class to delete a specified post.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class DeletePostQuery extends AbstractQuery<String>
{
	private boolean executed = false;
	private String result;
	private String userName;
	private String resourceHash;

	/**
    * Deletes a post.
    * 
    * @param userName
    *           the userName owning the post to deleted
    * @param resourceHash
    *           hash of the resource connected to the post
    * @throws IllegalArgumentException
    *            if userName or groupName are null or empty
    */
	public DeletePostQuery( String userName, String resourceHash ) throws IllegalArgumentException
	{
		if( userName == null || userName.length() == 0 ) throw new IllegalArgumentException( "no username given" );
		if( resourceHash == null || resourceHash.length() == 0 ) throw new IllegalArgumentException( "no resourcehash given" );
		this.userName = userName;
		this.resourceHash = resourceHash;
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
		result = performRequest( HttpMethod.DELETE, URL_USERS + "/" + userName + "/" + URL_POSTS + "/" + resourceHash, null );
	}
}

/*
 * $Log$
 * Revision 1.6  2006-06-14 18:23:21  mbork
 * refactored usage of username, password and host url
 *
 * Revision 1.5  2006/06/08 13:23:47  mbork
 * improved documentation, added throws statements even for runtimeexceptions, moved abstractquery to prevent users to call execute directly
 *
 * Revision 1.4  2006/06/08 08:02:54  mbork
 * fixed erroneous use of generics
 *
 * Revision 1.3  2006/06/08 07:41:12  mbork
 * client api completed
 *
 * Revision 1.2  2006/06/07 18:25:13  mbork
 * forgot checking constructor parameters
 *
 * Revision 1.1  2006/06/07 18:22:31  mbork
 * client api: finished implementing get and delete requests
 *
 */