package org.bibsonomy.lucene.database;

import java.util.Date;
import java.util.List;

import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.DBSessionFactory;
import org.bibsonomy.lucene.database.params.LuceneParam;
import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;

/**
 * class for accessing the bibsonomy database 
 * 
 * @author fei
 * @version $Id$
 * @param <R> the resource the logic handles
 */
public class LuceneDBLogic<R extends Resource> extends AbstractDatabaseManager implements LuceneDBInterface<R> {
	private Class<R> resourceClass;
	private DBSessionFactory sessionFactory;
	
	protected DBSession openSession() {
		return this.sessionFactory.getDatabaseSession();
	}
	
	@Override
	public Integer getLastTasId() {
		final DBSession session = this.openSession();
		try {
			return this.queryForObject("getLastTasId", Integer.class, session);
		} finally {
			session.close();
		}
	}
	
	@Override 
	public List<User> getPredictionForTimeRange(final Date fromDate) {
		final DBSession session = this.openSession();
		try {
			return this.queryForList("getPredictionForTimeRange", fromDate, User.class, session);
		} finally {
			session.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<LucenePost<R>> queryForLucenePosts(final String query, final Object param, final DBSession session) {
		return (List<LucenePost<R>>) this.queryForList(query, param, session);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.lucene.database.LuceneDBInterface#getPostsForUser(java.lang.String, java.lang.String, org.bibsonomy.common.enums.HashID, int, java.util.List, int, int)
	 */
	@Override
	public List<LucenePost<R>> getPostsForUser(final String userName, final int limit, final int offset) {
		final LuceneParam param = new LuceneParam();
		param.setUserName(userName);
		param.setLimit(limit);
		param.setOffset(offset);
		
		final DBSession session = this.openSession();
		try {
			return this.queryForLucenePosts("get" + this.getResourceName() + "ForUser", param, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.lucene.database.LuceneDBInterface#getNewestRecordDateFromTas()
	 */
	@Override
	public Date getNewestRecordDateFromTas() {
		final DBSession session = this.openSession();
		try {
			return this.queryForObject("getNewestRecordDateFromTas", Date.class, session);
		} finally {
			session.close();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.lucene.database.LuceneDBInterface#getContentIdsToDelete(java.util.Date)
	 */
	@Override
	public List<Integer> getContentIdsToDelete(final Date lastLogDate) {
		final DBSession session = this.openSession();
		try {
			return this.queryForList("get" + this.getResourceName() + "ContentIdsToDelete", lastLogDate, Integer.class, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.lucene.database.LuceneDBInterface#getLastLogDate()
	 */
	@Override
	public Date getLastLogDate() {
		final DBSession session = this.openSession();
		try {
			return this.queryForObject("getLastLog" + this.getResourceName(), Date.class, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.lucene.database.LuceneDBInterface#getNumberOfPosts()
	 */
	@Override
	public int getNumberOfPosts() {
		final DBSession session = this.openSession();
		try {
			return this.queryForObject("get" + this.getResourceName() + "Count", Integer.class, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.lucene.database.LuceneDBInterface#getPostEntries(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public List<LucenePost<R>> getPostEntries(final int lastContentId, final int max) {
		final LuceneParam param = new LuceneParam();
		param.setLastContentId(lastContentId);
		param.setLimit(max);
		
		final DBSession session = this.openSession();
		try {
			return this.queryForLucenePosts("get" + this.getResourceName() + "ForIndex", param, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.lucene.database.LuceneDBInterface#getNewPosts(java.lang.Integer)
	 */
	@Override
	public List<LucenePost<R>> getNewPosts(final Integer lastTasId) {
		final LuceneParam param = new LuceneParam();
		param.setLastTasId(lastTasId);
		
		final DBSession session = this.openSession();
		try {
			return this.queryForLucenePosts("get" + this.getResourceName() + "PostsForTimeRange", param, session);
		} finally {
			session.close();
		}
	}
	
	protected String getResourceName() {
		return this.resourceClass.getSimpleName();
	}

	/**
	 * @param resourceClass the resourceClass to set
	 */
	public void setResourceClass(final Class<R> resourceClass) {
		this.resourceClass = resourceClass;
	}

	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(final DBSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
