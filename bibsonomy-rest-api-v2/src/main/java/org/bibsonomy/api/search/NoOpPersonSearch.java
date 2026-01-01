package org.bibsonomy.api.search;

import org.bibsonomy.model.Person;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.query.PersonQuery;
import org.bibsonomy.model.statistics.Statistics;
import org.bibsonomy.services.searcher.PersonSearch;

import java.util.Collections;
import java.util.List;

/**
 * Minimal no-op person searcher that returns empty results. Used to satisfy
 * legacy search wiring without pulling in the legacy search stack.
 *
 * <p>All methods return non-null empty collections. This class is thread-safe
 * and can be used as a singleton.
 */
public class NoOpPersonSearch implements PersonSearch {

    /**
     * Returns an empty list of persons.
     * @param query the person query
     * @return non-null empty list
     */
    @Override
    public List<Person> getPersons(final PersonQuery query) {
        return Collections.emptyList();
    }

    /**
     * Returns empty statistics.
     * @param loggedinUser the logged-in user
     * @param query the person query
     * @return non-null fresh Statistics instance with default values
     */
    @Override
    public Statistics getStatistics(final User loggedinUser, final PersonQuery query) {
        return new Statistics();
    }
}
