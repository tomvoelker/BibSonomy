package org.bibsonomy.rest.client.queries.put;

import org.bibsonomy.common.JobResult;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.model.cris.CRISLink;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.util.ValidationUtils;

import java.io.StringWriter;
import java.util.Collections;

/**
 * query to update CRIS link
 *
 * @author pda
 */
public class UpdateCRISLinkQuery extends AbstractQuery<JobResult> {

	private final CRISLink link;

	public UpdateCRISLinkQuery(CRISLink link) {
		if (!ValidationUtils.present(link)) {
			throw new IllegalArgumentException("no link given.");
		}
		this.link = link;
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		final StringWriter sw = new StringWriter(100);
		this.getRenderer().serializeCRISLink(sw, this.link, null);
		this.downloadedDocument = performRequest(HttpMethod.PUT,
						this.getUrlRenderer().createUrlBuilderForCRISLinks().asString(), sw.toString());
	}

	@Override
	protected JobResult getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
		if (!isSuccess()) {
			return JobResult.buildFailure(Collections.singletonList(
							new ErrorMessage(getError(), "" + getStatusCode())));
		}
		return JobResult.buildSuccess();
	}
}
