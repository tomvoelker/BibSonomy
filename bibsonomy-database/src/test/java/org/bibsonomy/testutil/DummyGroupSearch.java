package org.bibsonomy.testutil;

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.database.services.GroupSearch;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.query.GroupQuery;

/**
 * dummy implementation of the group search
 *
 * @author dzo
 */
public class DummyGroupSearch implements GroupSearch {

	@Override
	public List<Group> getGroups(User loggedinUser, GroupQuery query) {
		return new LinkedList<>();
	}
}
