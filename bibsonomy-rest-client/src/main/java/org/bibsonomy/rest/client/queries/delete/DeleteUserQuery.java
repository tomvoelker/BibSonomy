package org.bibsonomy.rest.client.queries.delete;

import org.bibsonomy.common.enums.Status;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;

/**
 * Use this Class to delete a specified user.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class DeleteUserQuery extends AbstractQuery<String> {
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
	protected String doExecute() throws ErrorPerformingRequestException {
		this.downloadedDocument = performRequest(HttpMethod.DELETE, URL_USERS + "/" + this.userName, null);
		return null;
	}
	
	@Override
	public String getResult() throws BadRequestOrResponseException, IllegalStateException {
		if (this.isSuccess())
			return Status.OK.getMessage();
		return this.getError();
	}		
}