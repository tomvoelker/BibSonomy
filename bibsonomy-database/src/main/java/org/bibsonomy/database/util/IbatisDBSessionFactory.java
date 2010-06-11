package org.bibsonomy.database.util;

import org.bibsonomy.database.common.impl.AbstractDBSessionFactory;
import org.bibsonomy.database.common.util.IbatisUtils;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapSession;

/**
 * Factory for real database sessions via iBatis.
 * 
 * @author Jens Illig
 * @version $Id$
 */
public class IbatisDBSessionFactory extends AbstractDBSessionFactory {
	private static final SqlMapClient client = IbatisUtils.loadSqlMap("SqlMapConfig.xml");
	
	protected static final SqlMapClient getSqlMapClient() {
		return client;
	}
	
	@Override
	protected SqlMapSession getSqlMap() {
		return client.openSession();
	}
}
