package org.bibsonomy.lucene.database;

import java.io.Reader;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.lucene.database.params.GroupParam;
import org.bibsonomy.lucene.database.params.GroupTasParam;
import org.bibsonomy.lucene.database.params.ListParam;
import org.bibsonomy.lucene.database.params.TasParam;
import org.bibsonomy.model.Resource;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

/**
 * implements methods needed for index creation
 * @author fei
 *
 */
public abstract class LuceneDBGenerateLogic<R extends Resource> implements LuceneDBInterface<R> {
	private static final Log log = LogFactory.getLog(LuceneDBGenerateLogic.class);

	/** path to the ibatis database configuration file */
	private static final String SQL_MAP_CONFIG = "SqlMapConfig_lucene.xml";
	
	/** access to database */
	protected final SqlMapClient sqlMap;

	/**
	 * Constructor
	 */
	protected LuceneDBGenerateLogic() {
		try {
			// initialize database client for recommender logs
			String resource = SQL_MAP_CONFIG;
			Reader reader = Resources.getResourceAsReader (resource);
			sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
			log.info("Database connection initialized.");
		} catch (Exception e) {
			throw new RuntimeException ("Error initializing LuceneDBLogic class.", e);
		}
	}
	
	@Override
	public Integer getLastTasId() {
		Integer retVal = 0;
		try {
			retVal = (Integer)sqlMap.queryForObject("getLastTasId");
		} catch (SQLException e) {
			log.error("Error determining last tas entry.", e);
		}
		return retVal;
	}
	
	@Override
	public int getTasSize() {
		Integer retVal = 0;
		try {
			retVal = (Integer)sqlMap.queryForObject("getTasCount");
		} catch (SQLException e) {
			log.error("Error determining tas size.", e);
		}
		return retVal;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GroupParam> getGroupIDs() {
		List<GroupParam> retVal = null;
		try {
			retVal = (List<GroupParam>)sqlMap.queryForList("getGroupIDs");
		} catch (SQLException e) {
			log.error("Error getting group ids.", e);
			retVal = new LinkedList<GroupParam>();
		}
		
		return retVal;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TasParam> getTasEntries(Integer skip, Integer max) {
		ListParam param = new ListParam();
		param.setOffset(skip);
		param.setSize(max);
		
		List<TasParam> retVal = null;
		try {
			retVal = (List<TasParam>)sqlMap.queryForList("getTasEntries", param);
		} catch (SQLException e) {
			log.error("Error getting Tas entries.", e);
			retVal = new LinkedList<TasParam>();
		}
		
		return retVal;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TasParam> getGroupedTasEntries(int skip, int max) {
		ListParam param = new ListParam();
		param.setOffset(skip);
		param.setSize(max);
		
		List<TasParam> retVal = null;
		
		try {
			retVal = (List<TasParam>)sqlMap.queryForList("getGroupedTasEntries", param);
		} catch (SQLException e) {
			log.error("Error getting grouped tas entries", e);
			retVal = new LinkedList<TasParam>();
		}
		
		return retVal;
	};
	
	@SuppressWarnings("unchecked")
	@Override
	public List<GroupTasParam> getGroupTasEntries(Integer skip, Integer max) {
		ListParam param = new ListParam();
		param.setOffset(skip);
		param.setSize(max);
		List<GroupTasParam> retVal = null;
		
		try {
			retVal = (List<GroupTasParam>)sqlMap.queryForList("getGroupTasEntries", param);
		} catch (SQLException e) {
			log.error("Error getting group tas entries", e);
			retVal = new LinkedList<GroupTasParam>();
		}
		
		return retVal;
	}

}
