package org.bibsonomy.batch.searchlucene.database;

import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.batch.searchlucene.database.params.GroupParam;
import org.bibsonomy.batch.searchlucene.database.params.GroupTasParam;
import org.bibsonomy.batch.searchlucene.database.params.ListParam;
import org.bibsonomy.batch.searchlucene.database.params.TasParam;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

public class LuceneLogicImpl implements LuceneLogic {
	private static final Logger log = Logger.getLogger(LuceneLogicImpl.class);

	/** path to the ibatis database configuration file */
	private static final String SQLMAPFILE = "SqlMapConfig_lucene.xml";
	
	/** access to database */
	private final SqlMapClient sqlMap;

	private static LuceneLogicImpl instance = null;
	
	/**
	 * Constructor
	 */
	private LuceneLogicImpl() {
		try {
			// initialize database client for recommender logs
			String resource = SQLMAPFILE;
			Reader reader = Resources.getResourceAsReader (resource);
			sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
			log.info("Database [1] connection initialized.");
			
		} catch (Exception e) {
			throw new RuntimeException ("Error initializing database connection. Cause: " + e);
		}
	}
	
	/**
	 * Singleton instance creator
	 */
	public static LuceneLogic getInstance() {
		if (instance == null) instance = new LuceneLogicImpl();
		return instance;
	}
	
	@Override
	public int getTasSize() throws SQLException {
		Integer retVal = 0;
		retVal = (Integer)sqlMap.queryForObject("getTasCount");
		return retVal;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GroupParam> getGroupIDs() throws SQLException {
		return (List<GroupParam>)sqlMap.queryForList("getGroupIDs");
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TasParam> getTasEntries(Integer skip, Integer max)
			throws SQLException {
		ListParam param = new ListParam();
		param.setOffset(skip);
		param.setSize(max);
		return (List<TasParam>)sqlMap.queryForList("getTasEntries", param);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TasParam> getGroupedTasEntries(int skip, int max) throws SQLException {
		ListParam param = new ListParam();
		param.setOffset(skip);
		param.setSize(max);
		return (List<TasParam>)sqlMap.queryForList("getGroupedTasEntries", param);
	};
	
	@SuppressWarnings("unchecked")
	@Override
	public List<GroupTasParam> getGroupTasEntries(Integer skip, Integer max)
			throws SQLException {
		ListParam param = new ListParam();
		param.setOffset(skip);
		param.setSize(max);
		return (List<GroupTasParam>)sqlMap.queryForList("getGroupTasEntries", param);
	}

}
