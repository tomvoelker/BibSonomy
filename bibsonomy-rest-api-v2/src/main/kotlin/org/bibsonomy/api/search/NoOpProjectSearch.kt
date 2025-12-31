package org.bibsonomy.api.search

import org.bibsonomy.model.User
import org.bibsonomy.model.cris.Project
import org.bibsonomy.model.logic.query.ProjectQuery
import org.bibsonomy.model.statistics.Statistics
import org.bibsonomy.services.searcher.ProjectSearch
import org.bibsonomy.util.`object`.FieldDescriptor

/**
 * Minimal no-op implementation to satisfy legacy beans when search is disabled.
 */
class NoOpProjectSearch : ProjectSearch {

    override fun getProjects(loggedinUser: User?, query: ProjectQuery?): List<Project> {
        return emptyList()
    }

    override fun getStatistics(loggedinUser: User?, query: ProjectQuery?): Statistics {
        return Statistics(0)
    }

    override fun <E : Any?> getDistinctFieldValues(fieldDescriptor: FieldDescriptor<Project, E>?): Set<E> {
        return emptySet()
    }
}
