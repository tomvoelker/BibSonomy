package org.bibsonomy.rest.client.queries.delete;

import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;

/**
 * @author wla
 * @version $Id$
 */
public class UnpickClipboardQuery extends AbstractQuery<Integer> {

	private boolean clearAll;
	private String resourceHash;
	private String userName;

	@Override
	protected Integer doExecute() throws ErrorPerformingRequestException {
		final StringBuilder urlBuilder = new StringBuilder(RESTConfig.USERS_URL + "/" + userName + "/" + RESTConfig.CLIPBOARD_SUBSTRING);
		if (clearAll) {
			urlBuilder.append("?clear=true");
		} else {
			urlBuilder.append("/" + resourceHash);
		}
		performRequest(HttpMethod.DELETE, urlBuilder.toString(), null);
		return 0;
	}

	/**
	 * @param clearAll
	 *            the clearAll to set
	 */
	public void setClearAll(final boolean clearAll) {
		this.clearAll = clearAll;
	}

	/**
	 * @param resourceHash
	 *            the resourceHash to set
	 */
	public void setResourceHash(final String resourceHash) {
		this.resourceHash = resourceHash;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName(final String userName) {
		this.userName = userName;
	}

}
