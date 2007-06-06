package org.bibsonomy.rest.client.queries.delete;

import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.enums.HttpMethod;

/**
 * Use this Class to delete a specified group.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class DeleteGroupQuery extends AbstractQuery<String> {

	private boolean executed = false;
	private String result;
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
	public String getResult() {
		if (!this.executed) throw new IllegalStateException("Execute the query first.");
		return this.result;
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		this.executed = true;
		this.result = performRequest(HttpMethod.DELETE, URL_GROUPS + "/" + this.groupName, null);
	}
}