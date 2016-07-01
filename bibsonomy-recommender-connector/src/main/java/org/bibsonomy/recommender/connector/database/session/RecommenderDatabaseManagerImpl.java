/**
 * BibSonomy-Recommendation-Connector - Connector for the recommender framework for tag and resource recommendation
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
package org.bibsonomy.recommender.connector.database.session;

import java.util.List;

import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.DBSessionFactory;

import recommender.core.database.RecommenderDatabaseManager;
import recommender.core.model.Pair;

/**
 * This class wraps access to BibSonomy database classes.
 * 
 * @author lukas
 *
 */
public class RecommenderDatabaseManagerImpl extends AbstractDatabaseManager implements RecommenderDatabaseManager {

	private DBSessionFactory factory;
	
	/*
	 * (non-Javadoc)
	 * @see recommender.core.database.RecommenderDatabaseManager#processInsertQuery(java.lang.String, java.lang.Object)
	 */
	@Override
	public Object processInsertQuery(final String query, final Object param) {
		final DBSession session = factory.getDatabaseSession();
		try {
			return this.insert(query, param, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.database.RecommenderDatabaseManager#processQueryForObject(java.lang.Class, java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> T processQueryForObject(final Class<T> objectClass, final String query, final Object param) {
		final DBSession session = factory.getDatabaseSession();
		try {
			return this.queryForObject(query, param, objectClass, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.database.RecommenderDatabaseManager#processQueryForList(java.lang.Class, java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> List<T> processQueryForList(final Class<T> objectClass, final String query, final Object param) {
		final DBSession session = factory.getDatabaseSession();
		try {
			return this.queryForList(query, param, objectClass, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.database.RecommenderDatabaseManager#processUpdateQuery(java.lang.String, java.lang.Object)
	 */
	@Override
	public void processUpdateQuery(final String query, final Object param) {
		final DBSession session = factory.getDatabaseSession();
		try {
			this.update(query, param, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.database.RecommenderDatabaseManager#processDeleteQuery(java.lang.String, java.lang.Object)
	 */
	@Override
	public void processDeleteQuery(final String query, final Object param) {
		final DBSession session = factory.getDatabaseSession();
		try {
			this.delete(query, param, session);
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.database.RecommenderDatabaseManager#processBatchOfInsertQueries(java.util.List)
	 */
	@Override
	public Integer processBatchOfInsertQueries(final List<Pair<String, Object>> queryParameterMap) {
		final DBSession session = factory.getDatabaseSession();
		int counter = 0;
		try {
			session.beginTransaction();
			session.startBatch();
			for(Pair<String, Object> pair : queryParameterMap) {
				session.insert(pair.getFirst(), pair.getSecond());
				counter++;
			}
			session.executeBatch();
			session.commitTransaction();
		} finally {
			session.endTransaction();
			session.close();
		}
		return counter;
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.database.RecommenderDatabaseManager#processBatchOfUpdateQueries(java.util.List)
	 */
	@Override
	public Integer processBatchOfUpdateQueries(final List<Pair<String, Object>> queryParameterMap) {
		final DBSession session = factory.getDatabaseSession();
		int counter = 0;
		try {
			session.beginTransaction();
			session.startBatch();
			for(Pair<String, Object> pair : queryParameterMap) {
				session.update(pair.getFirst(), pair.getSecond());
				counter++;
			}
			session.executeBatch();
			session.commitTransaction();
		} finally {
			session.endTransaction();
			session.close();
		}
		return counter;
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.database.RecommenderDatabaseManager#processBatchOfDeleteQueries(java.util.List)
	 */
	@Override
	public Integer processBatchOfDeleteQueries(final List<Pair<String, Object>> queryParameterMap) {
		final DBSession session = factory.getDatabaseSession();
		int counter = 0;
		try {
			session.beginTransaction();
			session.startBatch();
			for(Pair<String, Object> pair : queryParameterMap) {
				session.delete(pair.getFirst(), pair.getSecond());
				counter++;
			}
			session.executeBatch();
			session.commitTransaction();
		} finally {
			session.endTransaction();
			session.close();
		}
		return counter;
	}
	
	/**
	 * @param factory the session factory to set
	 */
	public void setFactory(DBSessionFactory factory) {
		this.factory = factory;
	}

}
