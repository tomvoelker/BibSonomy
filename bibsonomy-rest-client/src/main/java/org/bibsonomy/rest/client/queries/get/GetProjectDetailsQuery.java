package org.bibsonomy.rest.client.queries.get;

import org.bibsonomy.model.cris.Project;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;

import java.io.StringWriter;

public class GetProjectDetailsQuery extends AbstractQuery<Project> {

    private final String projectId;

    public GetProjectDetailsQuery(String projectId) {
        this.projectId = projectId;
    }

    @Override
    protected void doExecute() throws ErrorPerformingRequestException {
        final StringWriter sw = new StringWriter(100);
        final Project project = new Project();
        project.setExternalId(projectId);
        getRenderer().serializeProject(sw, project, null);
        downloadedDocument = performGetRequest(getUrlRenderer().createUrlBuilderForProjects(projectId).asString());
    }

    @Override
    protected Project getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
        return getRenderer().parseProject(downloadedDocument);
    }
}
