package org.bibsonomy.api.search;

import java.util.Collections;
import java.util.List;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.query.GroupQuery;
import org.bibsonomy.services.searcher.GroupSearch;

/**
 * Minimal no-op implementation to satisfy legacy beans when search is disabled.
 */
public class NoOpGroupSearch implements GroupSearch {

	@Override
	public List<Group> getGroups(final User loggedinUser, final GroupQuery query) {
		return Collections.emptyList();
	}
}
