package org.bibsonomy.recommender.connector.database.session;

import java.util.List;
import java.util.Map;

import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.database.common.DBSession;

import recommender.core.database.RecommenderDBSession;

/**
 * wraps database access from recommender library on the bibsonomy database
 * 
 * @author Lukas
 *
 */
public class SQLMapRecommenderDBSessionWrapper implements RecommenderDBSession {

	private DBSession dbSession;
	
	public SQLMapRecommenderDBSessionWrapper(DBSession databaseSession) {
		this.dbSession = databaseSession;
	}

	@Override
	public void beginTransaction() {
		this.dbSession.beginTransaction();
	}

	@Override
	public void commitTransaction() {
		this.dbSession.commitTransaction();
	}

	@Override
	public void endTransaction() {
		this.dbSession.endTransaction();
	}

	@Override
	public void startBatch() {
		this.dbSession.startBatch();
	}

	@Override
	public void executeBatch() {
		this.dbSession.executeBatch();
	}

	@Override
	public void close() {
		this.dbSession.close();
	}

	@Override
	public void addError(String key, ErrorMessage errorMessage) {
		this.dbSession.addError(key, errorMessage);
	}

	@Override
	public Object queryForObject(String query, Object param) {
		return this.dbSession.queryForObject(query, param);
	}

	@Override
	public Object queryForObject(String query, Object param, Object store) {
		return this.dbSession.queryForObject(query, param, store);
	}

	@Override
	public List<?> queryForList(String query, Object param) {
		return this.dbSession.queryForList(query, param);
	}

	@Override
	public Map<?, ?> queryForMap(String query, Object param, String key) {
		return this.dbSession.queryForMap(query, param, key);
	}

	@Override
	public Map<?, ?> queryForMap(String query, Object param, String key,
			String value) {
		return this.dbSession.queryForMap(query, param, key, value);
	}

	@Override
	public Object insert(String query, Object param) {
		return this.dbSession.insert(query, param);
	}

	@Override
	public void update(String query, Object param) {
		this.dbSession.update(query, param);
	}

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
