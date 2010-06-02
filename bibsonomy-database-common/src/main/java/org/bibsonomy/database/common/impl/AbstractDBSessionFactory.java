package org.bibsonomy.database.common.impl;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.DBSessionFactory;

import com.ibatis.sqlmap.client.SqlMapSession;

/** 
 * @author dzo
 * @version $Id$
 */
public abstract class AbstractDBSessionFactory implements DBSessionFactory {
	
	@Override
	public DBSession getDatabaseSession() {
		return new DBSessionImpl(this.getSqlMap());
	}

	protected abstract SqlMapSession getSqlMap();
}
