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
		Object result = null;
		try {
			result = this.insert(query, param, session);
		} finally {
			session.close();
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.database.RecommenderDatabaseManager#processQueryForObject(java.lang.Class, java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> T processQueryForObject(final Class<T> objectClass, final String query, final Object param) {
		final DBSession session = factory.getDatabaseSession();
		T result = null;
		try {
			result = this.queryForObject(query, param, objectClass, session);
		} finally {
			session.close();
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.database.RecommenderDatabaseManager#processQueryForList(java.lang.Class, java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> List<T> processQueryForList(final Class<T> objectClass, final String query, final Object param) {
		final DBSession session = factory.getDatabaseSession();
		List<T> result = null;
		try {
			result = this.queryForList(query, param, objectClass, session);
		} finally {
			session.close();
		}
		return result;
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
