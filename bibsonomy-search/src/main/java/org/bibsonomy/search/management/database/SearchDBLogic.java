/**
 * BibSonomy Search - Helper classes for search modules.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package org.bibsonomy.search.management.database;

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
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.ResourcePersonRelationLogStub;
import org.bibsonomy.model.User;
import org.bibsonomy.search.SearchPost;
import org.bibsonomy.search.management.database.manager.PersonSearchDatabaseManager;
import org.bibsonomy.search.management.database.params.SearchParam;
import org.bibsonomy.search.update.SearchIndexSyncState;

/**
 * class for accessing the main database 
 * 
 * @author fei
 * @param <R> the resource the logic handles
 */
public class SearchDBLogic<R extends Resource> extends AbstractDatabaseManager implements SearchDBInterface<R> {
	private Class<R> resourceClass;
	private DBSessionFactory sessionFactory;
	private final PersonDatabaseManager personDatabaseManager = PersonDatabaseManager.getInstance();
	private final GeneralDatabaseManager generalDatabaseManager = GeneralDatabaseManager.getInstance();
	private final PersonSearchDatabaseManager personSearchDatabaseManager = PersonSearchDatabaseManager.getInstance();
	
	/**
	 * @return the session for the database
	 */
	protected DBSession openSession() {
		return this.sessionFactory.getDatabaseSession();
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
	private List<SearchPost<R>> queryForSearchPosts(final String query, final Object param, final DBSession session) {
		final List<SearchPost<R>> posts = (List<SearchPost<R>>) this.queryForList(query, param, session);
		// FIXME: remove ugly instance of check!
		if (BibTex.class.isAssignableFrom(this.resourceClass)) {
			setPersonRelations(posts, session);
		}
		return posts;
	}
	
	// FIXME: document why we have to query the database each time, and do not
	// query this information with a join
	private void setPersonRelations(final List<SearchPost<R>> posts, final DBSession session) {
		final HashMap<String, List<ResourcePersonRelation>> relationCache = new HashMap<>();
		for (SearchPost<R> post : posts) {
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
	}
	
	@Override
	public List<ResourcePersonRelation> getResourcePersonRelationsByPublication(String interHash) {
		final DBSession session = this.openSession();
		try {
			return this.personDatabaseManager.getResourcePersonRelationsByPublication(interHash, session);
		} finally {
			session.close();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.search.management.database.SearchDBInterface#getPostsForUser(java.lang.String, java.lang.String, org.bibsonomy.common.enums.HashID, int, java.util.List, int, int)
	 */
	@Override
	public List<SearchPost<R>> getPostsForUser(final String userName, final int limit, final int offset) {
		final SearchParam param = new SearchParam();
		param.setUserName(userName);
		param.setLimit(limit);
		param.setOffset(offset);
		
		final DBSession session = this.openSession();
		try {
			return this.queryForSearchPosts("get" + this.getResourceName() + "ForUser", param, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.search.management.database.SearchDBInterface#getNewestRecordDateFromTas()
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
	 * @see org.bibsonomy.search.management.database.SearchDBInterface#getContentIdsToDelete(java.util.Date)
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
	 * @see org.bibsonomy.search.management.database.SearchDBInterface#getNumberOfPosts()
	 */
	@Override
	public int getNumberOfPosts() {
		final DBSession session = this.openSession();
		try {
			return saveConvertToint(this.queryForObject("get" + this.getResourceName() + "Count", Integer.class, session));
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.search.management.database.SearchDBInterface#getPostEntries(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public List<SearchPost<R>> getPostEntries(final int lastContentId, final int max) {
		final SearchParam param = new SearchParam();
		param.setLastContentId(lastContentId);
		param.setLimit(max);
		
		final DBSession session = this.openSession();
		try {
			return this.queryForSearchPosts("get" + this.getResourceName() + "ForIndex", param, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.search.management.database.SearchDBInterface#getNewPosts(java.lang.Integer)
	 */
	@Override
	public List<SearchPost<R>> getNewPosts(final int lastTasId, final int limit, final int offset) {
		final SearchParam param = new SearchParam();
		param.setLastTasId(Integer.valueOf(lastTasId));
		param.setLimit(limit);
		param.setOffset(offset);
		
		final DBSession session = this.openSession();
		try {
			return this.queryForSearchPosts("get" + this.getResourceName() + "PostsForTimeRange", param, session);
		} finally {
			session.close();
		}
	}

	@Override
	public List<ResourcePersonRelationLogStub> getPubPersonRelationsByChangeIdRange(long fromPersonChangeId, long toPersonChangeIdExclusive) {
		final DBSession session = this.openSession();
		try {
			return personSearchDatabaseManager.getPubPersonChangesByChangeIdRange(fromPersonChangeId, toPersonChangeIdExclusive, session);
		} finally {
			session.close();
		}
	}

	@Override
	public List<PersonName> getPersonMainNamesByChangeIdRange(long firstChangeId, long toPersonChangeIdExclusive) {
		final DBSession session = this.openSession();
		try {
			return personSearchDatabaseManager.getPersonMainNamesByChangeIdRange(firstChangeId, toPersonChangeIdExclusive, session);
		} finally {
			session.close();
		}
	}

	@Override
	public List<Person> getPersonByChangeIdRange(long firstChangeId, long toPersonChangeIdExclusive) {
		final DBSession session = this.openSession();
		try {
			return personSearchDatabaseManager.getPersonByChangeIdRange(firstChangeId, toPersonChangeIdExclusive, session);
		} finally {
			session.close();
		}
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.management.database.SearchDBInterface#getDbState()
	 */
	@Override
	public SearchIndexSyncState getDbState() {
		final SearchIndexSyncState newState = new SearchIndexSyncState();
		newState.setLast_tas_id(this.getLastTasId());
		newState.setLast_log_date(this.getLastLogDate());
		newState.setLastPersonChangeId(this.getLastPersonChangeId());
		newState.setLastDocumentDate(this.getLastDocumentDate());
		return newState;
	}
	
	/**
	 * @return
	 */
	private Date getLastDocumentDate() {
		final DBSession session = this.openSession();
		try {
			return this.queryForObject("getLastDocumentDate", Date.class, session);
		} finally {
			session.close();
		}
	}

	/**
	 * @return the last tas id
	 */
	protected Integer getLastTasId() {
		final DBSession session = this.openSession();
		try {
			return this.queryForObject("getLastTasId", Integer.class, session);
		} finally {
			session.close();
		}
	}
	
	private Date getLastLogDate() {
		final DBSession session = this.openSession();
		try {
			final Date rVal = this.queryForObject("getLastLog" + this.getResourceName(), Date.class, session);
			if (rVal != null) {
				return rVal;
			}
			return new Date();
		} finally {
			session.close();
		}
	}
	
	private long getLastPersonChangeId() {
		final DBSession session = this.openSession();
		try {
			return this.generalDatabaseManager.getLastId(ConstantID.PERSON_CHANGE_ID, session).longValue();
		} finally {
			session.close();
		}
	}
	

	private String getResourceName() {
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
