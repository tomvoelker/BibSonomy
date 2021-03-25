package org.bibsonomy.database.managers.chain.project;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.logic.query.ProjectQuery;
import org.bibsonomy.model.logic.query.util.BasicQueryUtils;

import java.util.List;

/**
 * get all projects by the filters
 * @author dzo
 */
public class GetAllProjects extends ProjectChainElement {

	@Override
	protected List<Project> handle(final QueryAdapter<ProjectQuery> param, final DBSession session) {
		final ProjectQuery query = param.getQuery();
		return this.projectDatabaseManager.getAllProjects(query.getProjectStatus(), query.getOrder(), query.getSortOrder(), BasicQueryUtils.calcLimit(query), BasicQueryUtils.calcOffset(query), session);
	}

	@Override
	protected boolean canHandle(QueryAdapter<ProjectQuery> param) {
		return true;
	}
}
