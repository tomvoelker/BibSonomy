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

	public static List<Pair<String, SortOrder>> buildSortParameters(final List<SortCriteria> sortCriteria, final Class<?> type) {
		// TODO: maybe move to the ESPubSearch class @kch
		if (type.equals(BibTex.class)) {
			return buildPublicationSortParameters(sortCriteria);
		}

		return buildSortParameters(sortCriteria);
	}

	/**
	 * Takes a list of sort orders and creates a list of sort parameters.
	 * These are pairs contain the attribute names in the searchindex and
	 * the ascending or descding enum from elasticsearch.
	 *
	 * @param 	sortCriteria		list of sort criteriums
	 * @return	list of sort parameters
	 */
	public static List<Pair<String, SortOrder>> buildPublicationSortParameters(List<SortCriteria> sortCriteria) {
		final List<Pair<String, SortOrder>> sortParameters = new ArrayList<>();
		for (final SortCriteria sortCrit : sortCriteria) {
			final SortOrder esSortOrder = SortOrder.fromString(sortCrit.getSortOrder().toString());
			final SortKey sortKey = sortCrit.getSortKey();
			switch (sortKey) {
				// ignore these Order type since result of no sorting
				case RANK:
				case NONE:
					break;
				// Order type with cleaned up index attribute
				case TITLE:
					sortParameters.add(new Pair<>(Fields.Sort.TITLE, esSortOrder));
					break;
				case BOOKTITLE:
					sortParameters.add(new Pair<>(Fields.Sort.BOOKTITLE, esSortOrder));
					break;
				case JOURNAL:
					sortParameters.add(new Pair<>(Fields.Sort.JOURNAL, esSortOrder));
					break;
				case SERIES:
					sortParameters.add(new Pair<>(Fields.Sort.SERIES, esSortOrder));
					break;
				case PUBLISHER:
					sortParameters.add(new Pair<>(Fields.Sort.PUBLISHER, esSortOrder));
					break;
				case AUTHOR:
					sortParameters.add(new Pair<>(Fields.Sort.AUTHOR, esSortOrder));
					break;
				case EDITOR:
					sortParameters.add(new Pair<>(Fields.Sort.EDITOR, esSortOrder));
					break;
				case SCHOOL:
					sortParameters.add(new Pair<>(Fields.Sort.SCHOOL, esSortOrder));
					break;
				case INSTITUTION:
					sortParameters.add(new Pair<>(Fields.Sort.INSTITUTION, esSortOrder));
					break;
				case ORGANIZATION:
					sortParameters.add(new Pair<>("sort_" + sortKey.toString().toLowerCase(), esSortOrder));
					break;
				case PUBDATE:
					sortParameters.add(new Pair<>(Fields.Publication.YEAR, esSortOrder));
					sortParameters.add(new Pair<>(Fields.Publication.MONTH, reverseSortOrder(esSortOrder)));
					sortParameters.add(new Pair<>(Fields.Publication.DAY, esSortOrder));
					break;
				// more complex order types possible here
				default:
					sortParameters.add(new Pair<>(sortKey.toString().toLowerCase(), esSortOrder));
					break;
			}
		}
		return sortParameters;
	}

	/**
	 * Takes a list of sort orders and creates a list of sort parameters.
	 * These are pairs contain the attribute names in the searchindex and
	 * the ascending or descding enum from elasticsearch.
	 *
	 * This method only supports Order.TITLE and Order.DATE for building sorting parameters for any resource index.
	 *
	 * @param 	sortCriteria		list of sort criteriums
	 * @return	list of sort parameters
	 */
	public static List<Pair<String, SortOrder>> buildSortParameters(List<SortCriteria> sortCriteria) {
		final List<Pair<String, SortOrder>> sortParameters = new ArrayList<>();
		for (SortCriteria sortCrit : sortCriteria) {
			SortOrder esSortOrder = SortOrder.fromString(sortCrit.getSortOrder().toString());
			switch (sortCrit.getSortKey()) {
				// only supported order type for bookmarks
				case TITLE:
					sortParameters.add(new Pair<>(Fields.Sort.TITLE, esSortOrder));
					break;
				case DATE:
					sortParameters.add(new Pair<>(Fields.DATE, esSortOrder));
					break;
				default:
					break;
			}
		}
		return sortParameters;
	}

	/**
	 * Reverse the elastic search SortOrder enum.
	 *
	 * @param sortOrder	the sort orer to reverse
	 * @return DESC, if sortOrder is ASC. ASC, if sortOrder is DESC.
	 */
	public static SortOrder reverseSortOrder(SortOrder sortOrder) {
		if (sortOrder == SortOrder.DESC) {
			return SortOrder.ASC;
		}
		return SortOrder.DESC;
	}

}
