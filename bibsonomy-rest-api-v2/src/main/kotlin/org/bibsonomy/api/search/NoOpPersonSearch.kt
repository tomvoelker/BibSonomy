package org.bibsonomy.api.search

import org.bibsonomy.model.Person
import org.bibsonomy.model.User
import org.bibsonomy.model.logic.query.PersonQuery
import org.bibsonomy.model.statistics.Statistics
import org.bibsonomy.services.searcher.PersonSearch

/**
 * Minimal no-op implementation to satisfy legacy beans when search is disabled.
 */
class NoOpPersonSearch : PersonSearch {

    override fun getPersons(query: PersonQuery?): List<Person> {
        return emptyList()
    }

    override fun getStatistics(loggedinUser: User?, query: PersonQuery?): Statistics {
        return Statistics(0)
    }
}
