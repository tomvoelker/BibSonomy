package org.bibsonomy.rest.client.queries;

import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.enums.HttpMethod;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class CheckLoginQuery extends AbstractQuery<String> {

	private boolean executed = false;
	private String result;

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		this.executed = true;
		this.result = performRequest(HttpMethod.HEAD, URL_GROUPS, null);
	}

	@Override
	public String getResult() {
		if (!this.executed) throw new IllegalStateException("Execute the query first.");
		return this.result;
	}
}