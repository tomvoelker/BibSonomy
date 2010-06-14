package org.bibsonomy.lucene.database.util;

import org.bibsonomy.database.common.impl.AbstractDBSessionFactory;
import org.bibsonomy.database.common.util.IbatisUtils;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapSession;

/**
 * @author dzo
 * @version $Id$
 */
public class LuceneDatabaseSessionFactory extends AbstractDBSessionFactory {
	
	private static final SqlMapClient client = IbatisUtils.loadSqlMap("SqlMapConfig_lucene.xml");
	
	@Override
	protected SqlMapSession getSqlMap() {
		return client.openSession();
	}
}
