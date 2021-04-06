package org.bibsonomy.services.searcher;

import org.bibsonomy.model.User;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.logic.query.ProjectQuery;
import org.bibsonomy.model.statistics.Statistics;
import org.bibsonomy.util.object.FieldDescriptor;

import java.util.List;
import java.util.Set;

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

	/**
	 * stats about the projects
	 * @param loggedinUser
	 * @param query
	 * @return
	 */
	Statistics getStatistics(final User loggedinUser, final ProjectQuery query);

	/**
	 * returns all values for the specified field
	 * @param fieldDescriptor
	 * @return
	 */
	<E> Set<E> getDistinctFieldValues(final FieldDescriptor<Project, E> fieldDescriptor);
}