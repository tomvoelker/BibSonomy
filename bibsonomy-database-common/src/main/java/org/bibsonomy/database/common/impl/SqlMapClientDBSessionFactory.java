package org.bibsonomy.database.common.impl;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapSession;

/**
 * Factory for real database sessions
 * 
 * @author dzo
 * @version $Id$
 */
public class SqlMapClientDBSessionFactory extends AbstractDBSessionFactory {
	private SqlMapClient client;
	
	@Override
	protected SqlMapSession getSqlMap() {
		return this.client.openSession();
	}

	/**
	 * @param client the client to set
	 */
	public void setClient(final SqlMapClient client) {
		this.client = client;
	}
}
