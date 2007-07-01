package org.bibsonomy.rest.client.queries;

import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.enums.HttpMethod;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class CheckLoginQuery extends AbstractQuery<String> {

	@Override
	protected String doExecute() throws ErrorPerformingRequestException {
		return performRequest(HttpMethod.HEAD, URL_GROUPS, null);
	}
}