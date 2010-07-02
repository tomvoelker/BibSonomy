package org.bibsonomy.database.common.util;

import java.io.Reader;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

/**
 * @author dzo
 * @version $Id$
 */
public final class IbatisUtils {

	/**
	 * loads the specified iBatis config
	 * 
	 * @param filename
	 * @return the ibatis sql map
	 */
	public static SqlMapClient loadSqlMap(final String filename) {
		try {
			// initialize database client
			final Reader reader = Resources.getResourceAsReader(filename);
			return SqlMapClientBuilder.buildSqlMapClient(reader);
		} catch (final Exception e) {
			throw new RuntimeException("Error loading " + filename + " sqlmap.", e);
		}
	}

}
