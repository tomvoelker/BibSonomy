package org.bibsonomy.rest.client;

import org.bibsonomy.common.enums.Status;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;

/**
 * @author dzo
 */
public abstract class AbstractDeleteQuery extends AbstractQuery<String> {
	
	@Override
	public String getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
		if (this.isSuccess()) {
			return Status.OK.getMessage();
		}
		return this.getError();
	}
}
