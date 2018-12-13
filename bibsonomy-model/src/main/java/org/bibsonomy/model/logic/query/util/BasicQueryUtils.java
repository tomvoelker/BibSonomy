package org.bibsonomy.model.logic.query.util;

import org.bibsonomy.model.logic.query.BasicQuery;

/**
 * common util methods for a {@link BasicQuery}
 *
 * @author dzo
 */
public class BasicQueryUtils {

	private BasicQueryUtils() {
		// noop
	}

	/**
	 * calculates the offset for the query
	 * @param query
	 * @return
	 */
	public static int calcOffset(final BasicQuery query) {
		return query.getStart();
	}

	/**
	 * calulates the limit for the query
	 * @param query
	 * @return
	 */
	public static int calcLimit(final BasicQuery query) {
		return query.getEnd() - query.getStart();
	}

	/**
	 * sets the start and end parameter of the query based on limit and offset
	 * @param query
	 * @param limit
	 * @param offset
	 */
	public static void setStartAndEnd(BasicQuery query, int limit, int offset) {
		query.setStart(offset);
		query.setEnd(offset + limit);
	}
}
