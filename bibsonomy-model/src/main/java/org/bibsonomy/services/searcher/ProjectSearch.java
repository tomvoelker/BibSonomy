package org.bibsonomy.services.searcher;

import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.logic.query.ProjectQuery;

import java.util.List;

/**
 * search interface to search for {@link Project}s using the full text search
 *
 * @author dzo
 */
public interface ProjectSearch {

	/**
	 * @param loggedinUser the loggedin user
	 * @param query the query to filter the projects
	 * @return all matching projects
	 */
	List<Project> getProjects(final String loggedinUser, final ProjectQuery query);
}
