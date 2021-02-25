package org.bibsonomy.database.managers.chain.statistic.project;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.logic.query.ProjectQuery;
import org.bibsonomy.model.statistics.Statistics;

/**
 * counts for projects using the full text search
 *
 * @author dzo
 */
public class GetProjectsByFulltextSearchCount extends ProjectStatisticsChainElement {

	@Override
	protected Statistics handle(final QueryAdapter<ProjectQuery> param, DBSession session) {
		return this.projectDatabaseManager.getProjectsByFulltextSearchCount(param.getLoggedinUser(), param.getQuery());
	}

	@Override
	protected boolean canHandle(final QueryAdapter<ProjectQuery> param) {
		final ProjectQuery query = param.getQuery();
		return present(query.getType()) || present(query.getSearch()) || present(query.getPrefix()) || present(query.getOrganization());
	}
}
