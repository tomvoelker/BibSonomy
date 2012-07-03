package org.bibsonomy.logging;

import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.DBSessionFactory;

/**
 * 
 * @author sst
 * @version $Id$
 */
public class LoggingDatabaseManager extends AbstractDatabaseManager {
	
	private DBSessionFactory sessionFactory;
	
	private DBSession openSession() {
		return this.sessionFactory.getDatabaseSession();
	}
	
	/**
	 * inserts the log data into the db
	 * @param logdata
	 */
	public void insertLogdata(final LogData logdata) {
		final DBSession session = this.openSession();
		try {
			this.insert("BibLog.insertLogdata", logdata, session);
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
}