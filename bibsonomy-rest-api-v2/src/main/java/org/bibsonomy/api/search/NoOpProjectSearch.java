package org.bibsonomy.api.search;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bibsonomy.model.User;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.logic.query.ProjectQuery;
import org.bibsonomy.model.statistics.Statistics;
import org.bibsonomy.services.searcher.ProjectSearch;
import org.bibsonomy.util.object.FieldDescriptor;

/**
 * Minimal no-op implementation to satisfy legacy beans when search is disabled.
 */
public class NoOpProjectSearch implements ProjectSearch {

	@Override
	public List<Project> getProjects(final User loggedinUser, final ProjectQuery query) {
		return Collections.emptyList();
	}

	@Override
	public Statistics getStatistics(final User loggedinUser, final ProjectQuery query) {
		return new Statistics(0);
	}

	@Override
	public <E> Set<E> getDistinctFieldValues(final FieldDescriptor<Project, E> fieldDescriptor) {
		return Collections.emptySet();
	}
}
