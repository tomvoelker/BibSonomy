package org.bibsonomy.api.search;

import java.util.Collections;
import java.util.List;

import org.bibsonomy.model.Person;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.query.PersonQuery;
import org.bibsonomy.model.statistics.Statistics;
import org.bibsonomy.services.searcher.PersonSearch;

/**
 * Minimal no-op implementation to satisfy legacy beans when search is disabled.
 */
public class NoOpPersonSearch implements PersonSearch {

	@Override
	public List<Person> getPersons(final PersonQuery query) {
		return Collections.emptyList();
	}

	@Override
	public Statistics getStatistics(final User loggedinUser, final PersonQuery query) {
		return new Statistics(0);
	}
}
