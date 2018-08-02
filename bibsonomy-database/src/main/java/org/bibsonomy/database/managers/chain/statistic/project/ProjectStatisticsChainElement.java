package org.bibsonomy.database.managers.chain.statistic.project;

import org.bibsonomy.database.managers.ProjectDatabaseManager;
import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.model.logic.query.ProjectQuery;
import org.bibsonomy.model.statistics.Statistics;

/**
 *
 * @author dzo
 */
public abstract class ProjectStatisticsChainElement extends ChainElement<Statistics, ProjectQuery> {

	protected ProjectDatabaseManager projectDatabaseManager;

	/**
	 * @param projectDatabaseManager the projectDatabaseManager to set
	 */
	public void setProjectDatabaseManager(ProjectDatabaseManager projectDatabaseManager) {
		this.projectDatabaseManager = projectDatabaseManager;
	}
}
