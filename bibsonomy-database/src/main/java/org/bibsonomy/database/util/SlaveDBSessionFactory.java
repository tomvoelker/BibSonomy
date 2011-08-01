package org.bibsonomy.database.util;

import java.util.Properties;

import org.bibsonomy.database.common.impl.AbstractDBSessionFactory;
import org.bibsonomy.database.common.util.IbatisUtils;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapSession;

/**
 * TODO: replace with a more abstract db session factory when using mybatis
 * 
 * @author dzo
 * @version $Id$
 */
public class SlaveDBSessionFactory extends AbstractDBSessionFactory {
	
	private static final String JNDI_DATASOURCE = "java:comp/env/jdbc/bibsonomy_slave";
	private final static Properties props = new Properties();

	static {
		props.setProperty("JNDIDataSource", JNDI_DATASOURCE);
	}
	
	private static final SqlMapClient client = IbatisUtils.loadSqlMap("SqlMapConfig.xml", props);
	
	
	@Override
	protected SqlMapSession getSqlMap() {
		return client.openSession();
	}

}
