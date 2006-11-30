package org.bibsonomy.ibatis;
import java.io.Reader;

import junit.framework.TestCase;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;




/**
 * This class provides iBATIS-tests with the necessary SqlMapClient.
 * 
 * @author Christian Schenk
 */
public abstract class AbstractSqlMapTest extends TestCase {

	/** Communication with the database is done with this class */
	protected SqlMapClient sqlMap;

	@Override
	protected void setUp() throws Exception {
		final String resource = "SqlMapConfig.xml";
		final Reader reader = Resources.getResourceAsReader(resource);
		this.sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
		
	}

	@Override
	protected void tearDown() throws Exception {
		this.sqlMap = null;
	}	
}