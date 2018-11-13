package org.bibsonomy.rest.client.queries.post;

import org.bibsonomy.common.JobResult;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.model.cris.CRISLink;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.util.StringUtils;

import java.io.StringWriter;
import java.util.Collections;

/**
 * query to create new cris links
 *
 * @author pda
 */
public class CreateCRISLinkQuery extends AbstractQuery<JobResult> {

	private final CRISLink crisLink;

	public CreateCRISLinkQuery(CRISLink crisLink) {
		this.crisLink = crisLink;
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		final StringWriter sw = new StringWriter(100);
		getRenderer().serializeCRISLink(sw, crisLink, null);
		downloadedDocument = performRequest(HttpMethod.POST,
						getUrlRenderer().createUrlBuilderForCRISLinks().asString(),
						StringUtils.toDefaultCharset(sw.toString()));
	}

	@Override
	protected JobResult getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
		if (!isSuccess()) {
			return JobResult.buildFailure(Collections.singletonList(new ErrorMessage(getError(),
							"CRISLinkCreationError")));
		}
		return JobResult.buildSuccess();
	}
}
