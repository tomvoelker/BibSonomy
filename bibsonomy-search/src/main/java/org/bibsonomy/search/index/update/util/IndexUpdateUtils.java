package org.bibsonomy.search.index.update.util;

import org.bibsonomy.search.management.database.params.SearchParam;

import java.util.Date;

/**
 * some util methods for db update methods
 *
 * @author dzo
 */
public class IndexUpdateUtils {

	private IndexUpdateUtils() {
		// noop
	}

	/**
	 * builds the search param for the specified values
	 * @param lastEntityId
	 * @param lastLogDate
	 * @param size
	 * @param offset
	 * @return the search param
	 */
	public static SearchParam buildSeachParam(long lastEntityId, Date lastLogDate, int size, int offset) {
		final SearchParam param = new SearchParam();
		param.setLastContentId(lastEntityId);
		param.setLastLogDate(lastLogDate);
		param.setLimit(size);
		param.setOffset(offset);
		return param;
	}
}
