package org.bibsonomy.rest.client.queries.delete;

import org.bibsonomy.common.enums.Status;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;

/**
 * Use this Class to remove an user from a group.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class RemoveUserFromGroupQuery extends AbstractQuery<String> {
	private final String userName;
	private final String groupName;

	/**
	 * Remove an user from a group.
	 * 
	 * @param userName
	 *            the userName to be removed from the group
	 * @param groupName
	 *            group from which the user is to be removed
	 * @throws IllegalArgumentException
	 *             if userName or groupName are null or empty
	 */
	public RemoveUserFromGroupQuery(final String userName, final String groupName) throws IllegalArgumentException {
		if (userName == null || userName.length() == 0) throw new IllegalArgumentException("no username given");
		if (groupName == null || groupName.length() == 0) throw new IllegalArgumentException("no groupname given");

		this.userName = userName;
		this.groupName = groupName;
	}

	@Override
	protected String doExecute() throws ErrorPerformingRequestException {
		this.downloadedDocument = performRequest(HttpMethod.DELETE, URL_GROUPS + "/" + this.groupName + "/" + URL_USERS + "/" + this.userName, null);
		return null;
	}
	
	@Override
	public String getResult() throws BadRequestOrResponseException, IllegalStateException {
		if (this.isSuccess())
			return Status.OK.getMessage();
		return this.getError();
	}	
}