package org.bibsonomy.search.index.utils;

import org.bibsonomy.database.common.enums.CRISEntityType;
import org.bibsonomy.search.management.database.params.SearchParam;

import java.util.Date;

/**
 * @author dzo
 */
public final class SearchParamUtils {

	private SearchParamUtils() {
		// noop
	}

	/**
	 * builds a search param with the provided types
	 *
	 * @param sourceType the source type
	 * @param targetType the target type
	 * @return
	 */
	public static SearchParam buildParam(final CRISEntityType sourceType, final CRISEntityType targetType) {
		final SearchParam param = new SearchParam();
		param.setTargetType(targetType);
		param.setSourceType(sourceType);
		return param;
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

	/**
	 * builds the result map id for the cris entities
	 * @param sourceType
	 * @param targetType
	 * @return
	 */
	public static String buildResultMapID(CRISEntityType sourceType, CRISEntityType targetType) {
		return sourceType.toString() + "_" + targetType.toString();
	}
}
