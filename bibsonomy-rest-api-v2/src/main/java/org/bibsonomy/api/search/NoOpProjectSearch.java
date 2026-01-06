package org.bibsonomy.api.search;

import org.bibsonomy.model.User;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.logic.query.ProjectQuery;
import org.bibsonomy.model.statistics.Statistics;
import org.bibsonomy.services.searcher.ProjectSearch;
import org.bibsonomy.util.object.FieldDescriptor;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Minimal no-op project searcher that returns empty results. Used to satisfy
 * legacy search wiring without pulling in the legacy search stack.
 *
 * <p>All methods return non-null empty collections. This class is thread-safe
 * and can be used as a singleton.
 */
public class NoOpProjectSearch implements ProjectSearch {

    /**
     * Returns an empty list of projects.
     * @param loggedinUser the logged-in user
     * @param query the project query
     * @return non-null empty list
     */
    @Override
    public List<Project> getProjects(final User loggedinUser, final ProjectQuery query) {
        return Collections.emptyList();
    }

    /**
     * Returns empty statistics.
     * @param loggedinUser the logged-in user
     * @param query the project query
     * @return non-null fresh Statistics instance with default values
     */
    @Override
    public Statistics getStatistics(final User loggedinUser, final ProjectQuery query) {
        return new Statistics();
    }

    /**
     * Returns an empty set of distinct field values.
     * @param fieldDescriptor the field to get distinct values for
     * @return non-null empty set
     */
    @Override
    public <E> Set<E> getDistinctFieldValues(final FieldDescriptor<Project, E> fieldDescriptor) {
        return Collections.emptySet();
    }
}
