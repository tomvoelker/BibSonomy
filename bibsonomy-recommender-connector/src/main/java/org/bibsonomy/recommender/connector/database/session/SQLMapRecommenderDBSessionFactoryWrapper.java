package org.bibsonomy.recommender.connector.database.session;

import org.bibsonomy.database.common.impl.SqlMapClientDBSessionFactory;

import recommender.core.database.RecommenderDBSessionFactory;
import recommender.core.database.RecommenderDBSession;

/**
 * Wraps a {@link SqlMapClientDBSessionFactory} to inject those into the recommendation framework.
 * 
 * @author lukas
 *
 */
public class SQLMapRecommenderDBSessionFactoryWrapper implements RecommenderDBSessionFactory {

	private SqlMapClientDBSessionFactory sessionFactory;
	
	@Override
	public RecommenderDBSession getDatabaseSession() {
		
		return new SQLMapRecommenderDBSessionWrapper(sessionFactory.getDatabaseSession());
		
	}

	/**
	 * @return the sessionFactory
	 */
	public SqlMapClientDBSessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(SqlMapClientDBSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}
