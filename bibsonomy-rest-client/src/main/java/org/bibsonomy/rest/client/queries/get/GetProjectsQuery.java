package org.bibsonomy.rest.client.queries.get;

import org.apache.http.HttpStatus;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.logic.query.ProjectQuery;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.util.ValidationUtils;

import java.util.Collections;
import java.util.List;

public class GetProjectsQuery extends AbstractQuery<List<Project>> {
	private ProjectQuery query;

	public GetProjectsQuery(ProjectQuery query) {
		ValidationUtils.requirePresent(query, "Illegal ProjectQuery given.");
		this.query = query;
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		this.downloadedDocument = performGetRequest(getUrlRenderer().
						createUrlBuilderForProjects(this.query.getInternalId()).asString());
	}

	@Override
	protected List<Project> getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
		if (this.getHttpStatusCode() == HttpStatus.SC_NOT_FOUND) {
			return Collections.emptyList();
		}
		return this.getRenderer().parseProjects(this.downloadedDocument);
	}
}
