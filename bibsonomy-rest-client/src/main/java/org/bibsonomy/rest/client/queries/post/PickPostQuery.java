package org.bibsonomy.rest.client.queries.post;

import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;

/**
 * @author wla
 * @version $Id$
 */
public class PickPostQuery extends AbstractQuery<Integer> {

	private String userName;
	private String resourceHash;

	@Override
	protected Integer doExecute() throws ErrorPerformingRequestException {
		final String url = RESTConfig.USERS_URL + "/" + userName + "/" + RESTConfig.CLIPBOARD_SUBSTRING + "/" + resourceHash;
		performRequest(HttpMethod.POST, url, new String());
		return 0;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName(final String userName) {
		this.userName = userName;
	}

	/**
	 * @param resourceHash
	 *            the resourceHash to set
	 */
	public void setResourceHash(final String resourceHash) {
		this.resourceHash = resourceHash;
	}
}
