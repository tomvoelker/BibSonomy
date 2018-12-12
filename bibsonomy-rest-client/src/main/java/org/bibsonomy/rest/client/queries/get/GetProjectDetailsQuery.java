package org.bibsonomy.rest.client.queries.get;

import org.apache.http.HttpStatus;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.util.ValidationUtils;

/**
 * query to get a project
 *
 * @author pda
 */
public class GetProjectDetailsQuery extends AbstractQuery<Project> {

	private final String projectId;

	public GetProjectDetailsQuery(String projectId) {
		ValidationUtils.requirePresent(projectId, "No projectId given.");
		this.projectId = projectId;
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		this.downloadedDocument = performGetRequest(getUrlRenderer().createUrlBuilderForProjects(this.projectId).asString());
	}

	@Override
	protected Project getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
		if (this.getHttpStatusCode() == HttpStatus.SC_NOT_FOUND) {
			return null;
		}
		return getRenderer().parseProject(this.downloadedDocument);
	}
}
