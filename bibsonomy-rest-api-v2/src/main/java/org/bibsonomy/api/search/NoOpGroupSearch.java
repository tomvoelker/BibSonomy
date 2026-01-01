package org.bibsonomy.api.search;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.query.GroupQuery;
import org.bibsonomy.services.searcher.GroupSearch;

import java.util.Collections;
import java.util.List;

/**
 * Minimal no-op group searcher that returns empty results. Used to satisfy
 * legacy search wiring without pulling in the legacy search stack.
 *
 * <p>All methods return non-null empty collections. This class is thread-safe
 * and can be used as a singleton.
 */
public class NoOpGroupSearch implements GroupSearch {

    /**
     * Returns an empty list of groups.
     * @param loggedinUser the logged-in user
     * @param query the group query
     * @return non-null empty list
     */
    @Override
    public List<Group> getGroups(final User loggedinUser, final GroupQuery query) {
        return Collections.emptyList();
    }
}
