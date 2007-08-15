package org.bibsonomy.rest.client.queries.delete;

import org.bibsonomy.common.enums.Status;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;

/**
 * Use this Class to delete a specified group.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class DeleteGroupQuery extends AbstractQuery<String> {
	private final String groupName;

	/**
	 * Deletes the specified group.
	 * 
	 * @param groupName
	 *            the groupName of the group to be deleted
	 * @throws IllegalArgumentException
	 *             if the groupName is null or empty
	 */
	public DeleteGroupQuery(final String groupName) throws IllegalArgumentException {
		if (groupName == null || groupName.length() == 0) throw new IllegalArgumentException("no groupname given");

		this.groupName = groupName;
	}

	@Override
	protected String doExecute() throws ErrorPerformingRequestException {
		this.downloadedDocument = performRequest(HttpMethod.DELETE, URL_GROUPS + "/" + this.groupName, null);
		return null;
	}
	
	@Override
	public String getResult() throws BadRequestOrResponseException, IllegalStateException {
		if (this.isSuccess())
			return Status.OK.getMessage();
		return this.getError();
	}
}