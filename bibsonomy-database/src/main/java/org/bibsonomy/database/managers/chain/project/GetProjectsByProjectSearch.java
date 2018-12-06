package org.bibsonomy.database.managers.chain.project;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.logic.query.ProjectQuery;

import java.util.List;

/**
 * chain element to redirect the query to the project search
 *
 * @author dzo
 */
public class GetProjectsByProjectSearch extends ProjectChainElement {

	@Override
	protected List<Project> handle(final QueryAdapter<ProjectQuery> param, final DBSession session) {
		return this.projectDatabaseManager.getProjectsBySearch(param.getLoggedinUser(), param.getQuery());
	}

	@Override
	protected boolean canHandle(final QueryAdapter<ProjectQuery> param) {
		final ProjectQuery query = param.getQuery();
		return present(query.getType()) || present(query.getSearch()) || present(query.getPrefix());
	}
}
