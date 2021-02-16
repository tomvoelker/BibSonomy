package org.bibsonomy.testutil;

import org.bibsonomy.database.services.ProjectSearch;
import org.bibsonomy.model.User;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.logic.query.ProjectQuery;
import org.bibsonomy.model.statistics.Statistics;
import org.bibsonomy.util.object.FieldDescriptor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * dummy implementation for {@link ProjectSearch}
 *
 * @author dzo
 */
public class DummyProjectSearch implements ProjectSearch {

	@Override
	public List<Project> getProjects(final User loggedinUser, final ProjectQuery query) {
		return new LinkedList<>();
	}

	@Override
	public Statistics getStatistics(User loggedinUser, ProjectQuery query) {
		return new Statistics();
	}

	@Override
	public <E> Set<E> getDistinctFieldValues(FieldDescriptor<Project, E> fieldDescriptor) {
		return Collections.emptySet();
	}
}
