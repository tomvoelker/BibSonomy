/**
 * BibSonomy Search - Helper classes for search modules.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.search;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bibsonomy.database.common.AbstractDatabaseManagerWithSessionManagement;
import org.bibsonomy.database.common.DBSession;

/**
 * logic to get some basic simple information of
 * @author dzo
 */
public class SearchInfoDBLogic extends AbstractDatabaseManagerWithSessionManagement implements SearchInfoLogic {

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.SearchInfoLogic#getFriendsForUser(java.lang.String)
	 */
	@Override
	public Collection<String> getFriendsForUser(final String userName) {
		if (!present(userName)) {
			return Collections.emptySet();
		}

		try (final DBSession session = this.openSession()) {
			return this.queryForList("getFriendsForUser", userName, String.class, session);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.search.SearchInfoLogic#getGroupMembersByGroupName(java.lang.String)
	 */
	@Override
	public List<String> getGroupMembersByGroupName(final String groupName) {
		try (final DBSession session = this.openSession()) {
			return this.queryForList("getGroupMembersByGroupName", groupName, String.class, session); //TODO (AD) query for parent memberships
		}
	}

	@Override
	public List<String> getSubTagsForConceptTag(final String tag) {
		try (final DBSession session = this.openSession()) {
			return this.queryForList("getGlobalConceptByName", tag.toLowerCase(), String.class, session);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.SearchInfoLogic#getUserNamesThatShareDocumentsWithUser(java.lang.String)
	 */
	@Override
	public Set<String> getUserNamesThatShareDocumentsWithUser(String userName) {
		try (final DBSession session = this.openSession()) {
			final Set<String> users = new HashSet<>(this.getUserNamesThatShareDocumentsAsList(userName, session));
			users.add(userName);
			return users;
		}
	}

	@Override
	public Set<String> getPersonsOfOrganization(String organizationName) {
		try (final DBSession session = this.openSession()) {
			final List<String> personIds = this.queryForList("getPersonsForOrganization", organizationName, String.class, session);
			return new HashSet<>(personIds);
		}
	}

	/**
	 * @param userName
	 * @param session
	 * @return
	 */
	private List<String> getUserNamesThatShareDocumentsAsList(String userName, final DBSession session) {
		return this.queryForList("getDocumentUsers", userName, String.class, session);
	}
}
