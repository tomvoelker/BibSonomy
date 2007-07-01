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
public final class DeletePostQuery extends AbstractQuery<String> {
	private final String userName;
	private final String resourceHash;

	/**
	 * Deletes a post.
	 * 
	 * @param userName
	 *            the userName owning the post to deleted
	 * @param resourceHash
	 *            hash of the resource connected to the post
	 * @throws IllegalArgumentException
	 *             if userName or groupName are null or empty
	 */
	public DeletePostQuery(final String userName, final String resourceHash) throws IllegalArgumentException {
		if (userName == null || userName.length() == 0) throw new IllegalArgumentException("no username given");
		if (resourceHash == null || resourceHash.length() == 0) throw new IllegalArgumentException("no resourcehash given");

		this.userName = userName;
		this.resourceHash = resourceHash;
	}

	@Override
	protected String doExecute() throws ErrorPerformingRequestException {
		return performRequest(HttpMethod.DELETE, URL_USERS + "/" + this.userName + "/" + URL_POSTS + "/" + this.resourceHash, null);
	}
}