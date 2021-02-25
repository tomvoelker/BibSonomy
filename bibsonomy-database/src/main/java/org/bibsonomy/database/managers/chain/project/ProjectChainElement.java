package org.bibsonomy.database.managers.chain.project;

import org.bibsonomy.database.managers.ProjectDatabaseManager;
import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.logic.query.ProjectQuery;

import java.util.List;

/**
 * abstract project chain element
 *
 * @author dzo
 */
public abstract class ProjectChainElement extends ChainElement<List<Project>, QueryAdapter<ProjectQuery>> {

	/** the project database manager */
	protected ProjectDatabaseManager projectDatabaseManager;

	/**
	 * @param projectDatabaseManager the projectDatabaseManager to set
	 */
	public void setProjectDatabaseManager(final ProjectDatabaseManager projectDatabaseManager) {
		this.projectDatabaseManager = projectDatabaseManager;
	}
}
