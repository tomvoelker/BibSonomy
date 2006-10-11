package org.bibsonomy.ibatis;

import java.io.Reader;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

import junit.framework.TestCase;

public abstract class AbstractSqlMapTest extends TestCase {

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