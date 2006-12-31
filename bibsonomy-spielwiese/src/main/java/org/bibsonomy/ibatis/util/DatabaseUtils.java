package org.bibsonomy.ibatis.util;

import java.io.IOException;
import java.io.Reader;

import org.apache.log4j.Logger;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

/**
 * Methods concerning the database.
 *
 * @author Christian Schenk
 */
public class DatabaseUtils {

	/**
	 * Returns the SqlMapClient which can be used to query the database.
	 */
	public static SqlMapClient getSqlMapClient(final Logger log) {
		SqlMapClient rVal = null;
		try {
			final String resource = "SqlMapConfig.xml";
			final Reader reader = Resources.getResourceAsReader(resource);
			rVal = SqlMapClientBuilder.buildSqlMapClient(reader);
		} catch (final IOException ex) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, ex, "Couldn't initialize SqlMap");
		}
		return rVal;
	}
}