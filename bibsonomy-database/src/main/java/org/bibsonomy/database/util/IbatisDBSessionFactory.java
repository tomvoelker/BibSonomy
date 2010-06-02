package org.bibsonomy.database.util;

import org.bibsonomy.database.common.impl.AbstractDBSessionFactory;

import com.ibatis.sqlmap.client.SqlMapSession;

/**
 * Factory for real database sessions via iBatis.
 * 
 * @author Jens Illig
 * @version $Id$
 */
public class IbatisDBSessionFactory extends AbstractDBSessionFactory {

	@Override
	protected SqlMapSession getSqlMap() {
		return DatabaseUtils.getSqlMap();
	}
}
