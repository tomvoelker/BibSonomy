package org.bibsonomy.database.common;


/**
 * @author Jens Illig
 */
public interface DBSessionFactory {
	/**
	 * @return the produced DBSession
	 */
	public DBSession getDatabaseSession();
}