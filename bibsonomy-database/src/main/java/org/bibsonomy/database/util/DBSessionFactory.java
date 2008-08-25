package org.bibsonomy.database.util;

import org.bibsonomy.common.enums.DatabaseType;

/**
 * @author Jens Illig
 * @version $Id$
 */
public interface DBSessionFactory {
	/**
	 * @return the produced DBSession
	 */
	public DBSession getDatabaseSession();
	
	/**
	 * @param dbType the requested database type, e.g. MASTER
	 * @return the produced secondary DBSession
	 */
	public DBSession getDatabaseSession(DatabaseType dbType);
}