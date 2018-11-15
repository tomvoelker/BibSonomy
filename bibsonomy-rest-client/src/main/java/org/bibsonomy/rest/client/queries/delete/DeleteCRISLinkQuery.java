package org.bibsonomy.rest.client.queries.delete;

import org.bibsonomy.common.JobResult;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.model.cris.Linkable;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.util.ValidationUtils;

import java.util.Collections;

public class DeleteCRISLinkQuery extends AbstractQuery<JobResult> {
	private final Linkable source;
	private final Linkable target;

	public DeleteCRISLinkQuery(Linkable source, Linkable target) {
		this.source = ValidationUtils.requirePresent(source, "No source given.");
		this.target = ValidationUtils.requirePresent(target, "No target given.");
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		downloadedDocument = performRequest(HttpMethod.DELETE,
						getUrlRenderer().createUrlBuilderForCRISLinks(source.getLinkableId(),
										target.getLinkableId()).asString(), null);
	}

	@Override
	protected JobResult getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
		if (!isSuccess()) {
			return JobResult.buildFailure(
							Collections.singletonList(new ErrorMessage(getError(), "" + getStatusCode())));
		}
		return JobResult.buildSuccess();
	}
}
