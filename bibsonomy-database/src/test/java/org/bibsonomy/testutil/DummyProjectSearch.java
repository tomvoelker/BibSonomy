package org.bibsonomy.testutil;

import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.logic.query.ProjectQuery;
import org.bibsonomy.services.searcher.ProjectSearch;

import java.util.LinkedList;
import java.util.List;

/**
 * dummy implementation for {@link ProjectSearch}
 * @author dzo
 */
public class DummyProjectSearch implements ProjectSearch {

	@Override
	public List<Project> getProjects(final String loggedinUser, final ProjectQuery query) {
		return new LinkedList<>();
	}
}
