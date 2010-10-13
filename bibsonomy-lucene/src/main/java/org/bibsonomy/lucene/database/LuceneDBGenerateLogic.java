package org.bibsonomy.lucene.database;

import java.io.Reader;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.DBSessionFactory;
import org.bibsonomy.lucene.database.util.LuceneDatabaseSessionFactory;
import org.bibsonomy.model.Resource;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

/**
 * TODO: an AbstractDatabaseManager class e.g in bibsonomy-database-common
 * 
 * implements methods needed for index creation
 * 
 * @author fei
 * @version $Id$
 * 
 * @param <R> 
 */
public abstract class LuceneDBGenerateLogic<R extends Resource> extends AbstractDatabaseManager implements LuceneDBInterface<R> {
	private static final Log log = LogFactory.getLog(LuceneDBGenerateLogic.class);

	/** path to the ibatis database configuration file */
	private static final String SQL_MAP_CONFIG = "SqlMapConfig_lucene.xml";
	
	/** access to database */
	@Deprecated // use openSession instead
	protected final SqlMapClient sqlMap;

	private DBSessionFactory sessionFactory;

	/**
	 * Constructor
	 */
	protected LuceneDBGenerateLogic() {
		this.sessionFactory = new LuceneDatabaseSessionFactory();
		try {
			// initialize database client for lucene queries
			final Reader reader = Resources.getResourceAsReader(SQL_MAP_CONFIG);
			sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
			log.info("Database connection initialized.");
		} catch (Exception e) {
			throw new RuntimeException("Error initializing LuceneDBGenerateLogic class.", e);
		}
	}
	
	protected DBSession openSession() {
		return this.sessionFactory.getDatabaseSession();
	}
	
	@Override
	public Integer getLastTasId() {
		try {
			return (Integer)sqlMap.queryForObject("getLastTasId");
		} catch (SQLException e) {
			log.error("Error determining last tas entry.", e);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override 
	public List<String> getSpamPredictionForTimeRange(Date fromDate) {	
		try {
			return sqlMap.queryForList("getSpamPredictionForTimeRange", fromDate);
		} catch (SQLException e) {
			log.error("Error getting flagged users", e);
		}
		
		return new LinkedList<String>();
	}
	
	@SuppressWarnings("unchecked")
	@Override 
	public List<String> getNonSpamPredictionForTimeRange(Date fromDate) {		
		try {
			return sqlMap.queryForList("getNonSpamPredictionForTimeRange", fromDate);
		} catch (SQLException e) {
			log.error("Error getting unflagged users", e);
		}
		
		return new LinkedList<String>();
	}
}
