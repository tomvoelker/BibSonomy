package org.bibsonomy.database.managers.chain.statistic.project;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.logic.query.ProjectQuery;
import org.bibsonomy.model.statistics.Statistics;

/**
 * @author dzo
 */
public class GetAllProjectsCount extends ProjectStatisticsChainElement {

	@Override
	protected Statistics handle(final QueryAdapter<ProjectQuery> param, final DBSession session) {
		return this.projectDatabaseManager.getAllProjectsCounts(param.getQuery().getProjectStatus(), session);
	}

	@Override
	protected boolean canHandle(final QueryAdapter<ProjectQuery> param) {
		return true;
	}
}
