/**
 * BibSonomy Search - Helper classes for search modules.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
import java.util.List;

import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.DBSessionFactory;

/**
 * 
 * @author dzo
 */
public class SearchInfoDBLogic extends AbstractDatabaseManager implements SearchInfoLogic {	
	private DBSessionFactory sessionFactory;
	
	private DBSession openSession() {
		return this.sessionFactory.getDatabaseSession();
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.lucene.database.LuceneInfoLogic#getFriendsForUser(java.lang.String)
	 */
	@Override
	public Collection<String> getFriendsForUser(final String userName) {
		if (!present(userName)) {
			return Collections.emptySet();
		}
		
		final DBSession session = this.openSession();
		try {
			return this.queryForList("getFriendsForUser", userName, String.class, session);
		} finally {
			session.close();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.lucene.database.LuceneInfoLogic#getGroupMembersByGroupName(java.lang.String)
	 */
	@Override
	public List<String> getGroupMembersByGroupName(final String groupName) {
		final DBSession session = this.openSession();
		try {
			return this.queryForList("getGroupMembersByGroupName", groupName, String.class, session);
		} finally {
			session.close();
		}
	}

	@Override
	public List<String> getSubTagsForConceptTag(final String tag) {
		final DBSession session = this.openSession();
		try {
			return this.queryForList("getGlobalConceptByName", tag.toLowerCase(), String.class, session);
		} finally {
			session.close();
		}
	}

	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(final DBSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
