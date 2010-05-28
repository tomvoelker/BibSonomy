package org.bibsonomy.community.database;

import java.io.Reader;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.community.database.param.CommunityParam;
import org.bibsonomy.community.database.param.CommunityResourceParam;
import org.bibsonomy.community.model.Post;
import org.bibsonomy.community.util.CommunityBase;
import org.bibsonomy.model.Resource;

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
		loadConfiguration();
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
	// helper functions
	//------------------------------------------------------------------------
	protected <T> Collection<T> ensureList(Collection<T> posts) {
		if( posts==null )
			return new LinkedList<T>();
		else
			return posts;
	}

	@SuppressWarnings("unchecked")
	protected <T> Collection<T> queryForList(final String queryName, CommunityResourceParam<?> param) {
		param.setCommunityDBName(getCommunityDBName());
		Collection<T> retVal = null;
		try {
			retVal = (Collection<T>)getSqlMap().queryForList(queryName, param);
		} catch (Exception e) {
			log.error("Error fetching resources in query '"+queryName+"'", e);
		}
		
		return retVal;
	}

	/**
	 * fetch a list 
	 * FIXME: make a proper parameter inheritance hierachie so that we don't need 
	 *        to clone this function  
	 * @param <T>
	 * @param queryName
	 * @param param
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T> Collection<T> queryForList(final String queryName, CommunityParam param) {
		Collection<T> retVal = null;
		try {
			retVal = (Collection<T>)getSqlMap().queryForList(queryName, param);
		} catch (Exception e) {
			log.error("Error fetching resources in query '"+queryName+"'", e);
		}
		
		return retVal;
	}
	
	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------
	public SqlMapClient getSqlMap() {
		return sqlMap;
	}
}
