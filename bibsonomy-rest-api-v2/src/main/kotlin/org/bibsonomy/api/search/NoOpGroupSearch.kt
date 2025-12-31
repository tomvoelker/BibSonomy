package org.bibsonomy.api.search

import org.bibsonomy.model.Group
import org.bibsonomy.model.User
import org.bibsonomy.model.logic.query.GroupQuery
import org.bibsonomy.services.searcher.GroupSearch

/**
 * Minimal no-op implementation to satisfy legacy beans when search is disabled.
 */
class NoOpGroupSearch : GroupSearch {

    override fun getGroups(loggedinUser: User?, query: GroupQuery?): List<Group> {
        return emptyList()
    }
}
