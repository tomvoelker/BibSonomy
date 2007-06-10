/*
 * Created on 10.06.2007
 */
package org.bibsonomy.database.util;

public interface DBSessionFactory {
	public Transaction getDatabaseSession();
}
