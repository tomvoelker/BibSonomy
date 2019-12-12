package org.bibsonomy.search.es.util;

import org.bibsonomy.common.Pair;
import org.elasticsearch.search.sort.SortOrder;

import java.util.ArrayList;
import java.util.List;

public class SortingUtils {

	/**
	 * Takes a sort order and creates a list of sort parameters.
	 * These are pairs contain the attribute names in the searchindex and
	 * the ascending or descding enum from elasticsearch.
	 *
	 * @param 	sortOrder
	 * @return	list of sort parameters
	 */
	public static List<Pair<String, SortOrder>> buildSortParameters(org.bibsonomy.model.SortOrder sortOrder) {
		List<Pair<String, SortOrder>> sortParameters = new ArrayList<>();
		SortOrder esSortOrder = SortOrder.fromString(sortOrder.getDirection().toString());
		switch (sortOrder.getOrder()) {
			// ignore these Order type since result of no sorting
			case RANK:
			case NONE:
				break;
			case PUBDATE:
				sortParameters.add(new Pair<>("year", esSortOrder));
				sortParameters.add(new Pair<>("month", reverseSortOrder(esSortOrder)));
				sortParameters.add(new Pair<>("day", esSortOrder));
				break;
			case AUTHOR:
				sortParameters.add(new Pair<>("authors.name", esSortOrder));
				break;
			// more complex order types possible here
			default:
				sortParameters.add(new Pair<>(sortOrder.getOrder().toString().toLowerCase(), esSortOrder));
				break;
		}
		return sortParameters;
	}

	/**
	 * Takes a sort order and creates a list of sort parameters.
	 * These are pairs contain the attribute names in the searchindex and
	 * the ascending or descding enum from elasticsearch.
	 *
	 * This method only supports Order.TITLE and Order.DATE for building sorting parameters for the bookmark index.
	 *
	 * @param 	sortOrder
	 * @return	list of sort parameters
	 */
	public static List<Pair<String, SortOrder>> buildBookmarkSortParameters(org.bibsonomy.model.SortOrder sortOrder) {
		List<Pair<String, SortOrder>> sortParameters = new ArrayList<>();
		SortOrder esSortOrder = SortOrder.fromString(sortOrder.getDirection().toString());
		switch (sortOrder.getOrder()) {
			// only supported order type for bookmarks
			case TITLE:
			case DATE:
				sortParameters.add(new Pair<>(sortOrder.getOrder().toString().toLowerCase(), esSortOrder));
				break;
			default:
				break;
		}
		return sortParameters;
	}

	public static SortOrder reverseSortOrder(SortOrder sortOrder) {
		if (sortOrder == SortOrder.DESC) {
			return SortOrder.ASC;
		}
		return SortOrder.DESC;
	}
}
