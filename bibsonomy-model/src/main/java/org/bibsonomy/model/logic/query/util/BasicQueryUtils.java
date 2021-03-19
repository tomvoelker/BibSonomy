package org.bibsonomy.model.logic.query.util;

import org.bibsonomy.model.logic.query.BasicQuery;
import org.bibsonomy.model.logic.query.PaginatedQuery;

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
	public static int calcOffset(final PaginatedQuery query) {
		return query.getStart();
	}

	/**
	 * calulates the limit for the query
	 * @param query
	 * @return
	 */
	public static int calcLimit(final PaginatedQuery query) {
		return query.getEnd() - query.getStart();
	}

	/**
	 * calulates the limit for the query
	 * @param query
	 * @param maxResultWindow
	 * @return
	 */
	public static int calcLimit(final PaginatedQuery query, final int maxResultWindow) {
		final int end = Math.min(query.getEnd(), maxResultWindow);
		return end - query.getStart();
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
