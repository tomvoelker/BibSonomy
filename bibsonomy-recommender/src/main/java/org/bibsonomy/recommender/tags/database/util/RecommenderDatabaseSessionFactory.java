package org.bibsonomy.recommender.tags.database.util;

import org.bibsonomy.database.common.impl.AbstractDBSessionFactory;
import org.bibsonomy.database.common.util.IbatisUtils;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapSession;

/**
 * @author dzo
 * @version $Id$
 */
public class RecommenderDatabaseSessionFactory extends AbstractDBSessionFactory {

	private static final SqlMapClient SQL_MAP = IbatisUtils.loadSqlMap("SqlMapConfig_recommender.xml");
	
	@Override
	protected SqlMapSession getSqlMap() {
		return SQL_MAP.openSession();
	}

}
