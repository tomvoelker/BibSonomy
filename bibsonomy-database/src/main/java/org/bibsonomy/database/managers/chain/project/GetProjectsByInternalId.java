package org.bibsonomy.database.managers.chain.project;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.logic.query.ProjectQuery;

/**
 * chain element to get all projects by the internal id
 *
 * @author dzo
 */
public class GetProjectsByInternalId extends ProjectChainElement {

	@Override
	protected List<Project> handle(final QueryAdapter<ProjectQuery> param, final DBSession session) {
		return this.projectDatabaseManager.getProjectsByInternalId(param.getQuery().getInternalId(), session);
	}

	@Override
	protected boolean canHandle(QueryAdapter<ProjectQuery> param) {
		final ProjectQuery query = param.getQuery();
		return present(query.getInternalId());
	}
}
