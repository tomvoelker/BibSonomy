package org.bibsonomy.database.util;

/**
 * @author Jens Illig
 * @version $Id$
 */
public interface DBSessionFactory {
	/**
	 * @return the produced DBSession
	 */
	public DBSession getDatabaseSession();
}