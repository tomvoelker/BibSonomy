package org.bibsonomy.database.util;

/**
 * @author Jens Illig
 * @version $Id$
 */
public interface DBSessionFactory {
	public DBSession getDatabaseSession();
}