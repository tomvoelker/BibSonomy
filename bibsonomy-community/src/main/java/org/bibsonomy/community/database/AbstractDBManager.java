package org.bibsonomy.community.database;

import java.io.Reader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.community.util.CommunityBase;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

/**
 * base class for database managers
 * @author fei
 *
 */
public abstract class AbstractDBManager extends CommunityBase {
	private final static Log log = LogFactory.getLog(AbstractDBManager.class);

	/** ibatis sqlmap client */
	private final SqlMapClient sqlMap;
	
	/**
	 * constructor for initializing the database connection
	 * 
	 * @param sqlMapFile
	 */
	protected AbstractDBManager(String sqlMapFile) {
		try {
			// initialize database client for recommender logs
			String resource = sqlMapFile;
			Reader reader = Resources.getResourceAsReader (resource);
			sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
			log.info("Database connection initialized.");
		} catch (Exception e) {
			throw new RuntimeException ("Error initializing database connection.", e);
		}
	}

	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------
	public SqlMapClient getSqlMap() {
		return sqlMap;
	}
}
