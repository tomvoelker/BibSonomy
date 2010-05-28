package org.bibsonomy.database.util;

import org.bibsonomy.common.enums.DatabaseType;

/**
 * Factory for real database sessions via iBatis.
 * 
 * @author Jens Illig
 * @version $Id$
 */
public class IbatisDBSessionFactory implements DBSessionFactory {

	public DBSession getDatabaseSession() {
		return new DBSessionImpl(DatabaseUtils.getSqlMap());
	}
	
	@Override
	@Deprecated
	public DBSession getDatabaseSession(DatabaseType dbType) {
		if (dbType.equals(DatabaseType.MASTER)) {
			return new DBSessionImpl(DatabaseUtils.getSqlMap(DatabaseType.MASTER));
		}
		if (dbType.equals(DatabaseType.SLAVE)) {
			return new DBSessionImpl(DatabaseUtils.getSqlMap(DatabaseType.SLAVE));
		}
		// default: master connection
		return new DBSessionImpl(DatabaseUtils.getSqlMap(DatabaseType.MASTER));
	}

}