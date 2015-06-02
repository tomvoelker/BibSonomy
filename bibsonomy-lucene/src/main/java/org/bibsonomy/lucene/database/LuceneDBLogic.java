/**
 * BibSonomy-Lucene - Fulltext search facility of BibSonomy
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
package org.bibsonomy.lucene.database;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.DBSessionFactory;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.database.managers.GeneralDatabaseManager;
import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.lucene.database.params.LuceneParam;
import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.User;

/**
 * class for accessing the bibsonomy database 
 * 
 * @author fei
 * @param <R> the resource the logic handles
 */
public class LuceneDBLogic<R extends Resource> extends AbstractDatabaseManager implements LuceneDBInterface<R> {
	private Class<R> resourceClass;
	private DBSessionFactory sessionFactory;
	private final PersonDatabaseManager personDatabaseManager = PersonDatabaseManager.getInstance();
	private final GeneralDatabaseManager generalDatabaseManager = GeneralDatabaseManager.getInstance();
	
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
		final List<LucenePost<R>> posts = (List<LucenePost<R>>) this.queryForList(query, param, session);
		final HashMap<String, List<ResourcePersonRelation>> relationCache = new HashMap<>();
		for (LucenePost<R> post : posts) {
			final String interHash = post.getResource().getInterHash();
			List<ResourcePersonRelation> rels = relationCache.get(interHash);
			if (rels == null) {
				rels = this.personDatabaseManager.getResourcePersonRelationsByPublication(interHash, session);
				if (rels == null) {
					rels = Collections.emptyList();
				}
				relationCache.put(interHash, rels);
			}
			post.setResourcePersonRelations(rels);
		}
		return posts;
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
	 * @see org.bibsonomy.lucene.database.LuceneDBInterface#getPostEntriesOrderedByHash(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public List<LucenePost<R>> getPostEntriesOrderedByHash(int lastOffset, int max) {
		final LuceneParam param = new LuceneParam();
		param.setLastOffset(lastOffset);
		param.setLimit(max);
		
		final DBSession session = this.openSession();
		try {
			return this.queryForLucenePosts("get" + this.getResourceName() + "ForIndexOrderedByHash", param, session);
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
	
	public String getResourceName() {
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

	/* (non-Javadoc)
	 * @see org.bibsonomy.lucene.database.LuceneDBInterface#getLastPersonChangeId()
	 */
	@Override
	public long getLastPersonChangeId() {
		final DBSession session = this.openSession();
		try {
			return this.generalDatabaseManager.getLastId(ConstantID.PERSON_CHANGE_ID, session);
		} finally {
			session.close();
		}
	}
}
