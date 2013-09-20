package org.bibsonomy.recommender.connector.database.session;

import java.util.List;
import java.util.Map;

import org.bibsonomy.database.common.DBSession;

import recommender.core.database.RecommenderDBSession;
import recommender.core.error.ErrorMessage;

/**
 * Wraps database access via {@link DBSession} on BibSonomy's database to inject it into the recommender framework.
 * 
 * @author Lukas
 *
 */
public class SQLMapRecommenderDBSessionWrapper implements RecommenderDBSession {

	private DBSession dbSession;
	
	public SQLMapRecommenderDBSessionWrapper(DBSession databaseSession) {
		this.dbSession = databaseSession;
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.database.RecommenderDBSession#beginTransaction()
	 */
	@Override
	public void beginTransaction() {
		this.dbSession.beginTransaction();
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.database.RecommenderDBSession#commitTransaction()
	 */
	@Override
	public void commitTransaction() {
		this.dbSession.commitTransaction();
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.database.RecommenderDBSession#endTransaction()
	 */
	@Override
	public void endTransaction() {
		this.dbSession.endTransaction();
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.database.RecommenderDBSession#startBatch()
	 */
	@Override
	public void startBatch() {
		this.dbSession.startBatch();
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.database.RecommenderDBSession#executeBatch()
	 */
	@Override
	public void executeBatch() {
		this.dbSession.executeBatch();
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.database.RecommenderDBSession#close()
	 */
	@Override
	public void close() {
		this.dbSession.close();
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.database.RecommenderDBSession#addError(java.lang.String, recommender.core.error.ErrorMessage)
	 */
	@Override
	public void addError(String key, ErrorMessage errorMessage) {
		this.dbSession.addError(key, new org.bibsonomy.common.errors.ErrorMessage(errorMessage.getDefaultMessage(),
				errorMessage.getErrorCode(), errorMessage.getParameters()));
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.database.RecommenderDBSession#queryForObject(java.lang.String, java.lang.Object)
	 */
	@Override
	public Object queryForObject(String query, Object param) {
		return this.dbSession.queryForObject(query, param);
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.database.RecommenderDBSession#queryForObject(java.lang.String, java.lang.Object, java.lang.Object)
	 */
	@Override
	public Object queryForObject(String query, Object param, Object store) {
		return this.dbSession.queryForObject(query, param, store);
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.database.RecommenderDBSession#queryForList(java.lang.String, java.lang.Object)
	 */
	@Override
	public List<?> queryForList(String query, Object param) {
		return this.dbSession.queryForList(query, param);
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.database.RecommenderDBSession#queryForMap(java.lang.String, java.lang.Object, java.lang.String)
	 */
	@Override
	public Map<?, ?> queryForMap(String query, Object param, String key) {
		return this.dbSession.queryForMap(query, param, key);
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.database.RecommenderDBSession#queryForMap(java.lang.String, java.lang.Object, java.lang.String, java.lang.String)
	 */
	@Override
	public Map<?, ?> queryForMap(String query, Object param, String key,
			String value) {
		return this.dbSession.queryForMap(query, param, key, value);
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.database.RecommenderDBSession#insert(java.lang.String, java.lang.Object)
	 */
	@Override
	public Object insert(String query, Object param) {
		return this.dbSession.insert(query, param);
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.database.RecommenderDBSession#update(java.lang.String, java.lang.Object)
	 */
	@Override
	public void update(String query, Object param) {
		this.dbSession.update(query, param);
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.database.RecommenderDBSession#delete(java.lang.String, java.lang.Object)
	 */
	@Override
	public void delete(String query, Object param) {
		this.dbSession.delete(query, param);
	}

	/**
	 * @return the dbSession
	 */
	public DBSession getDbSession() {
		return dbSession;
	}

	/**
	 * @param dbSession the dbSession to set
	 */
	public void setDbSession(DBSession dbSession) {
		this.dbSession = dbSession;
	}

	
	
}
