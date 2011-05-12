package org.bibsonomy.database.common.util;

import java.io.Reader;
import java.util.Properties;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

/**
 * @author dzo
 * @version $Id$
 */
public final class IbatisUtils {

    private static final Properties props = new Properties();
    static {
	props.setProperty("JNDIDataSource", "java:comp/env/jdbc/bibsonomy");
    }
    
    /**
     * loads the specified iBatis config
     * 
     * @param filename
     * @return the ibatis sql map
     */
    public static SqlMapClient loadSqlMap(final String filename) {
	return loadSqlMap(filename, props);
    }

    /**
     * loads the specified iBatis config
     * 
     * @param filename
     * @param props - the properties to specify the JNDI datasource using the key "JNDIDataSource"
     * @return the ibatis sql map
     */
    public static SqlMapClient loadSqlMap(final String filename, final Properties props) {
	try {
	    // initialize database client
	    final Reader reader = Resources.getResourceAsReader(filename);
	    return SqlMapClientBuilder.buildSqlMapClient(reader, props);
	} catch (final Exception e) {
	    throw new RuntimeException("Error loading " + filename + " sqlmap.", e);
	}
    }
}
