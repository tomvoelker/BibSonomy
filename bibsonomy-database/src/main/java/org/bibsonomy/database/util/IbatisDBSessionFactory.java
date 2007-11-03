package org.bibsonomy.database.util;


/**
 * Factory for real database sessions via IBATIS
 *
 * @version $Id$
 * @author  Jens Illig
 * $Author$
 */
public class IbatisDBSessionFactory implements DBSessionFactory {
	
	public DBSession getDatabaseSession() {
		return new DBSessionImpl(DatabaseUtils.getSqlMap());
	}
}
