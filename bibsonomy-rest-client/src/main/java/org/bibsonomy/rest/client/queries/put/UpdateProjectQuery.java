package org.bibsonomy.rest.client.queries.put;

import org.bibsonomy.common.JobResult;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.util.ValidationUtils;

import java.io.StringWriter;
import java.util.Collections;

/**
 * query to update a project
 *
 * @author pda
 */
public class UpdateProjectQuery extends AbstractQuery<JobResult> {
	private String projectId;
	private Project project;

	public UpdateProjectQuery(String projectId, Project project) {
		ValidationUtils.requirePresent(projectId, "No externalId present.");
		ValidationUtils.requirePresent(project, "No project given.");
		this.projectId = projectId;
		this.project = project;
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		final StringWriter sw = new StringWriter(100);
		this.getRenderer().serializeProject(sw, project, null);
		final String projectUrl = this.getUrlRenderer().createUrlBuilderForProjectsExternalId(this.projectId).asString();
		this.downloadedDocument = performRequest(HttpMethod.PUT, projectUrl, sw.toString());
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
