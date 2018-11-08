package org.bibsonomy.search.es.search.project;

import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.logic.query.ProjectQuery;
import org.bibsonomy.services.searcher.ProjectSearch;

import java.util.List;

/**
 * elasticsearch implementation of the {@link ProjectSearch}
 *
 * @author dzo
 */
public class ElasticsearchProjectSearch implements ProjectSearch {

	@Override
	public List<Project> getProjects(String loggedinUser, ProjectQuery query) {
		return null;
	}

}
