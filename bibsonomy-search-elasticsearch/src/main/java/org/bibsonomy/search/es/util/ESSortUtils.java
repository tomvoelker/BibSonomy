package org.bibsonomy.search.es.util;

import org.bibsonomy.common.Pair;
import org.bibsonomy.common.SortCriteria;
import org.bibsonomy.common.enums.SortKey;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.search.es.ESConstants.Fields;
import org.elasticsearch.search.sort.SortOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kch
 */
public class ESSortUtils {

	/**
	 * Reverse the elastic search SortOrder enum.
	 *
	 * @param sortOrder	the sort orer to reverse
	 * @return DESC, if sortOrder is ASC. ASC, if sortOrder is DESC.
	 */
	// TODO maybe move
	public static SortOrder reverseSortOrder(SortOrder sortOrder) {
		if (sortOrder == SortOrder.DESC) {
			return SortOrder.ASC;
		}
		return SortOrder.DESC;
	}

}
