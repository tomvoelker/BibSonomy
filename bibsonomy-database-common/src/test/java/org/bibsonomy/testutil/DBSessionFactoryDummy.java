package org.bibsonomy.testutil;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.DBSessionFactory;

/**
 * a dummy implementation for {@link DBSessionFactory}
 * useful for tests
 * 
 * @author dzo
 */
public class DBSessionFactoryDummy implements DBSessionFactory {

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.common.DBSessionFactory#getDatabaseSession()
	 */
	@Override
	public DBSession getDatabaseSession() {
		return null;
	}
}
