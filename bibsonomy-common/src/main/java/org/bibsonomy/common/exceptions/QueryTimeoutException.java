package org.bibsonomy.common.exceptions;

import java.sql.SQLException;

/**
 * @author Dominik Benz <benz@cs.uni-kassel.de>
 * @version $Id$
 */
public class QueryTimeoutException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private String queryId = "";
	
	/**
	 * Constructs a new query timeout exception, which is basically an SQL exception
	 * with the name of the timed out query
     *
     * @param   message   the detail message. The detail message is saved for 
     *          later retrieval by the {@link #getMessage()} method.
     */
	public QueryTimeoutException(Exception ex, String query) {
		super(ex);
		this.queryId = query;
	}
		
}