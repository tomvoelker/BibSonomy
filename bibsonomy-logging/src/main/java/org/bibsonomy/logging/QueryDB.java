package org.bibsonomy.logging;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.SQLException;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

/**
 * 
 * @author sst
 * @version $Id$
 */
public class QueryDB {
	private static QueryDB instance = null;
	
	/**
	 * @return the {@link QueryDB} instance
	 * @throws IOException
	 */
	public static QueryDB getInstance() throws IOException {
		if (instance == null) {
			instance = new QueryDB();
		}
		return instance;
	}
	
	private final SqlMapClient sqlMapClient;
	
	/**
	 * Private constructor = singleton
	 * @throws IOException 
	 */
	private QueryDB() throws IOException {
		final Reader reader = new InputStreamReader(QueryDB.class.getClassLoader().getResourceAsStream("SqlMapConfigLogger.xml"));
		sqlMapClient = SqlMapClientBuilder.buildSqlMapClient(reader);
	}
	
	/**
	 * inserts the log data into the db
	 * @param logdata
	 * @throws SQLException
	 */
	public void insertLogdata(Log logdata) throws SQLException {
		sqlMapClient.insert("BibLog.insertLogdata", logdata);
	}
}