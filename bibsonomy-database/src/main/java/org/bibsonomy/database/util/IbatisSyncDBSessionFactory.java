package org.bibsonomy.database.util;

import java.util.Properties;

import org.bibsonomy.database.common.impl.AbstractDBSessionFactory;
import org.bibsonomy.database.common.util.IbatisUtils;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapSession;

/**
 * Factory for real database sessions via iBatis.
 * 
 * @author Jens Illig
 * @version $Id$
 */
public class IbatisSyncDBSessionFactory extends AbstractDBSessionFactory {
    	
    	private final static Properties props = new Properties();
    	
    	static {
    		// TODO replace biblicious with other service with PUMA, specified in context.xml 
    	   props.setProperty("JNDIDataSource", "java:comp/env/jdbc/biblicious");
    	}
    	
	private static final SqlMapClient client = IbatisUtils.loadSqlMap("SqlMapConfig.xml", props);
	
	protected static final SqlMapClient getSqlMapClient() {
		return client;
	}
	
	@Override
	protected SqlMapSession getSqlMap() {
		return client.openSession();
	}
	
	/**
	 * Set JNDIDataSource like  props.setProperty("JNDIDataSource", "java:comp/env/jdbc/[serviceName]");
	 * service name must be specified in context.xml
	 * @return properties to set JNDIDataSource
	 */
	public Properties getProps(){
		return props;
	}
}
