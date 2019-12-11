package org.bibsonomy.search.es.util;

import org.bibsonomy.common.Pair;
import org.elasticsearch.search.sort.SortOrder;

import java.util.ArrayList;
import java.util.List;

public class SortingUtils {

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
				sortParameters.add(new Pair<>("month", esSortOrder));
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

}
