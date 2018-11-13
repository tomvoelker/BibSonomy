package org.bibsonomy.rest.client.queries.get;

import org.bibsonomy.model.cris.Project;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;

/**
 * query to get a project
 *
 * @author pda
 */
public class GetProjectDetailsQuery extends AbstractQuery<Project> {

	private final String projectId;

	public GetProjectDetailsQuery(String projectId) {
		this.projectId = projectId;
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		this.downloadedDocument = performGetRequest(getUrlRenderer().createUrlBuilderForProjects(this.projectId).asString());
	}

	@Override
	protected Project getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
		return getRenderer().parseProject(this.downloadedDocument);
	}
}
