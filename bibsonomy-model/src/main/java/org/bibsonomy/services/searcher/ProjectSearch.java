package org.bibsonomy.services.searcher;

import org.bibsonomy.model.User;
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
	 * @param loggedinUser the logged in user (full details)
	 * @param query the query to filter the projects
	 * @return all matching projects
	 */
	List<Project> getProjects(final User loggedinUser, final ProjectQuery query);
}
