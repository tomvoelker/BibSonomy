package org.bibsonomy.util;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.bibsonomy.common.exceptions.QueryTimeoutException;

/**
 * Convenience methods to throw exceptions.
 *
 * @author Christian Schenk
 * @version $Id$
 */
public class ExceptionUtils {

	/**
	 * Like the name suggests this method logs an error and throws a
	 * RuntimeException attached with the initial exception.
	 * @param log the logger instance to use
	 * @param ex the exception to log an rethrow wrapped
	 * @param error message of the new RuntimeException
	 * @throws RuntimeException the resulting exception
	 */
	public static void logErrorAndThrowRuntimeException(final Logger log, final Exception ex, final String error) throws RuntimeException {
		log.error(error + " - throwing RuntimeException" + ((ex != null) ? ("\n" + ex.toString()) : ""), ex );
		throw new RuntimeException(error, ex);
	}
	
	/**
	 * throw query timeout exception
	 * 
	 * @param log
	 * @param ex
	 * @param query
	 * @throws QueryTimeoutException
	 */
	public static void logErrorAndThrowQueryTimeoutException(final Logger log, final Exception ex, final String query) throws QueryTimeoutException {
		log.debug("Query timeout for query: " + query);
		throw new QueryTimeoutException(ex, query);
	} 
}