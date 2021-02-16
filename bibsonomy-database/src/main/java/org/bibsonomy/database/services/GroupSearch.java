package org.bibsonomy.database.services;

import java.util.List;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.query.GroupQuery;

/**
 * interface for group search
 *
 * @author dzo
 */
public interface GroupSearch {

	/**
	 * @param loggedinUser the loggedin user
	 * @param query the query to filter the projects
	 * @return all matching groups
	 */
	List<Group> getGroups(final User loggedinUser, final GroupQuery query);
}