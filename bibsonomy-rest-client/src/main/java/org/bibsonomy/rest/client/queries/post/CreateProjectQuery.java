package org.bibsonomy.rest.client.queries.post;

import org.bibsonomy.common.JobResult;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.util.StringUtils;

import java.io.StringWriter;
import java.util.Collections;

/**
 * query to create a project
 *
 * @author pda
 */
public class CreateProjectQuery extends AbstractQuery<JobResult> {

	private final Project project;

	public CreateProjectQuery(Project project) {
		this.project = project;
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		final StringWriter sw = new StringWriter(100);
		getRenderer().serializeProject(sw, project, null);
		downloadedDocument = performRequest(HttpMethod.POST,
						getUrlRenderer().createUrlBuilderForProjects().asString(),
						StringUtils.toDefaultCharset(sw.toString()));
	}

	@Override
	protected JobResult getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
		if (!isSuccess()) {
			return JobResult.buildFailure(Collections.singletonList(new ErrorMessage(getError(),
							"projectCreationError")));
		}
		return JobResult.buildSuccess(getRenderer().parseProjectId(downloadedDocument));
	}
}
