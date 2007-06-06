package org.bibsonomy.rest.client.queries.delete;

import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.enums.HttpMethod;

/**
 * Use this Class to delete a specified user.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class DeleteUserQuery extends AbstractQuery<String> {

	private boolean executed = false;
	private String result;
	private final String userName;

	/**
	 * Deletes an account of a bibsonomy user.
	 * 
	 * @param userName
	 *            the userName of the user to be deleted
	 * @throws IllegalArgumentException
	 *             if userName is null or empty
	 */
	public DeleteUserQuery(final String userName) throws IllegalArgumentException {
		if (userName == null || userName.length() == 0) throw new IllegalArgumentException("no username given");

		this.userName = userName;
	}

	@Override
	public String getResult() {
		if (!this.executed) throw new IllegalStateException("Execute the query first.");
		return this.result;
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		this.executed = true;
		this.result = performRequest(HttpMethod.DELETE, URL_USERS + "/" + this.userName, null);
	}
}