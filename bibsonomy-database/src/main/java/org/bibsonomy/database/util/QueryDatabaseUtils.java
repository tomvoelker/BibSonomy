package org.bibsonomy.database.util;

import org.bibsonomy.model.logic.query.ProjectQuery;

/**
 * util mathods
 *
 * @author dzo
 */
public class QueryDatabaseUtils {

	public static final int DEFAULT_LIST_LIMIT = 10;

	/**
	 * calcs the limit offset
	 * @param param
	 * @return
	 */
	public static int calulateLimit(ProjectQuery param) {
		int limit = param.getEnd() - param.getStart();
		if (limit < 0) {
			return DEFAULT_LIST_LIMIT;
		}

		return limit;
	}

	/**
	 * calculates the offset
	 * @param param
	 * @return
	 */
	public static int calulateOffset(final ProjectQuery param) {
		return param.getStart();
	}
}
