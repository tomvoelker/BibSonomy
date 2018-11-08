package org.bibsonomy.database.common;


/**
 * an abstract database implementation with a DBSesssionFactory
 * @author dzo
 */
public abstract class AbstractDatabaseManagerWithSessionManagement extends AbstractDatabaseManager {

	private DBSessionFactory sessionFactory;

	/** opens a new db session */
	protected DBSession openSession() {
		return this.sessionFactory.getDatabaseSession();
	}

	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(DBSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
