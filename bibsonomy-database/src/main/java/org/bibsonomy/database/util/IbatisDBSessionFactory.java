package org.bibsonomy.database.util;

/**
 * Factory for real database sessions via IBATIS
 *
 * @author  Jens Illig
 * @version $Id$
 */
public class IbatisDBSessionFactory implements DBSessionFactory {
	
	public DBSession getDatabaseSession() {
		return new DBSessionImpl(DatabaseUtils.getSqlMap());
	}
}