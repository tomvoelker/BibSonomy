package org.bibsonomy.database.managers.chain.project;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.util.QueryDatabaseUtils;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.logic.query.ProjectQuery;

import java.util.List;

/**
 * get all projects by the filters
 * @author dzo
 */
public class GetAllProjects extends ProjectChainElement {

	@Override
	protected List<Project> handle(final ProjectQuery param, final DBSession session) {
		return this.projectDatabaseManager.getAllProjects(param.getProjectStatus(), param.getOrder(), param.getSortOrder(), QueryDatabaseUtils.calulateLimit(param), QueryDatabaseUtils.calulateOffset(param), session);
	}

	@Override
	protected boolean canHandle(ProjectQuery param) {
		return true;
	}
}
