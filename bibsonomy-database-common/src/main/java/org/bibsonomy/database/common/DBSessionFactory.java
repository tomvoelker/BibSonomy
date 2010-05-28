package org.bibsonomy.database.common;


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