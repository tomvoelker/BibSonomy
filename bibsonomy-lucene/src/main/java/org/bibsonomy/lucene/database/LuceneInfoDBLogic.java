package org.bibsonomy.lucene.database;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.DBSessionFactory;
import org.bibsonomy.model.Tag;

/**
 * 
 * @author dzo
 * @version $Id$
 */
public class LuceneInfoDBLogic extends AbstractDatabaseManager implements LuceneInfoLogic {	
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

	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(final DBSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public List<String> getSubTagsForConceptTag(String tag) {
		final DBSession session = this.openSession();
		try {
			return this.queryForList("getGlobalConceptByName", tag.toLowerCase(), String.class, session);
		} finally {
			session.close();
		}
	}
}
