package org.bibsonomy.database.managers.chain.statistic.project;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.model.logic.query.ProjectQuery;
import org.bibsonomy.model.statistics.Statistics;

/**
 * @author dzo
 */
public class GetAllProjectsCount extends ProjectStatisticsChainElement {

	@Override
	protected Statistics handle(ProjectQuery param, DBSession session) {
		return this.projectDatabaseManager.getAllProjectsCounts(param.getProjectStatus(), session);
	}

	@Override
	protected boolean canHandle(ProjectQuery param) {
		return true;
	}
}
