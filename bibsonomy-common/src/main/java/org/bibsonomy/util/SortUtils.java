/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.common.SortCriteria;
import org.bibsonomy.common.enums.SortKey;
import org.bibsonomy.common.enums.SortOrder;

/**
 * Convenience methods for sorting lists
 *
 * @author Dominik Benz
 */
public class SortUtils {

	private static final String SORT_KEY_DELIMITER       = "|";
	private static final String SORT_ORDER_DELIMITER       = "|";
	
		
	/**
	 * parse a list of sort keys (delimited by SORT_KEY_DELIMITER)
	 * 
	 * @param sortKeys
	 * @return a list of sort keys
	 */
	public static List<SortKey> parseSortKeys(final String sortKeys) {
		final List<SortKey> parsedSortKeys = new LinkedList<>();
		if (sortKeys == null) {
			return parsedSortKeys;
		}
		for (String sortKey : sortKeys.split("\\" + SORT_KEY_DELIMITER)) {
			if (sortKey.equalsIgnoreCase("relevance")) {
				parsedSortKeys.add(SortKey.RANK);
			} else {				
				parsedSortKeys.add(EnumUtils.searchEnumByName(SortKey.values(), sortKey));
			}
		}
		return parsedSortKeys;
	}
	
	/**
	 * parse a list of sort oders (delimited by SORT_ORDER_DELIMITER) 
	 * 
	 * @param sortOrders
	 * @return a list of sort orders
	 */
	public static List<SortOrder> parseSortOrders(final String sortOrders) {
		final List<SortOrder> parsedSortOrders = new LinkedList<>();
		if (sortOrders == null) {
			return parsedSortOrders;
		}
		for (String sortOrder : sortOrders.split("\\" + SORT_ORDER_DELIMITER)) {
			parsedSortOrders.add(EnumUtils.searchEnumByName(SortOrder.values(), sortOrder));
		}
		return parsedSortOrders;
	}

	/**
	 * Generate a list of sort criteria from two separate sort key and order lists.
	 * If there are less sort orders than the keys, the order for the remaining keys will be set
	 * to the first order in the list.
	 *
	 * @param sortKeys
	 * @param sortOrders
	 * @return
	 */
	public static List<SortCriteria> generateSortCriteria(List<SortKey> sortKeys, List<SortOrder> sortOrders) {
		List<SortCriteria> sortCriteria = new LinkedList<>();
		// Check, if any sort keys given
		if (sortKeys.isEmpty()) {
			return sortCriteria;
		}
		// Check, if there is enough sort orders for each key
		if (sortOrders.size() >= sortKeys.size()) {
			// Create pair-wise sort criteria
			Iterator<SortKey> sortKeysIt = sortKeys.iterator();
			Iterator<SortOrder> sortOrderIt = sortOrders.iterator();
			while (sortKeysIt.hasNext() && sortOrderIt.hasNext()) {
				sortCriteria.add(new SortCriteria(sortKeysIt.next(), sortOrderIt.next()));
			}

		} else {
			// Not enough sort orders, take first sort order for all keys
			SortOrder sortOrder = sortOrders.get(0);
			for (SortKey sortKey : sortKeys) {
				sortCriteria.add(new SortCriteria(sortKey, sortOrder));
			}
		}
		return sortCriteria;
	}

	/**
	 * Util method to extract just the sort key of every sort criteria in the provided list.
	 * Used to build parameter values for URLs
	 *
	 * @param sortCriteria
	 * @return list of sort keys separated by delimiter
	 */
	public static String getSortKeys(List<SortCriteria> sortCriteria) {
		final List<String> sortKeys = new LinkedList<>();
		for (final SortCriteria criteria : sortCriteria) {
			sortKeys.add(criteria.getSortKey().toString());
		}
		return StringUtils.implodeStringArray(sortKeys.toArray(), SORT_KEY_DELIMITER);
	}

	/**
	 * Util method to extract just the sort order of every sort criteria in the provided list.
	 * Used to build parameter values for URLs
	 *
	 * @param sortCriteria
	 * @return list of sort order separated by delimiter
	 */
	public static String getSortOrders(List<SortCriteria> sortCriteria) {
		final List<String> sortOrders = new LinkedList<>();
		for (final SortCriteria criteria : sortCriteria) {
			sortOrders.add(criteria.getSortOrder().toString());
		}
		return StringUtils.implodeStringArray(sortOrders.toArray(), SORT_ORDER_DELIMITER);
	}

	/**
	 * returns a single sort criteria
	 * @param key
	 * @return
	 */
	public static List<SortCriteria> singletonSortCriteria(SortKey key, SortOrder order) {
		return Collections.singletonList(new SortCriteria(key, order));
	}

	/**
	 * returns a single sort criteria, defaulting to descending order
	 * @param key
	 * @return
	 */
	public static List<SortCriteria> singletonSortCriteria(SortKey key) {
		return singletonSortCriteria(key, SortOrder.DESC);
	}

	public static SortKey getFirstSortKey(List<SortCriteria> sortCriteria) {
		if (ValidationUtils.present(sortCriteria)) {
			return sortCriteria.get(0).getSortKey();
		}
		return null;
	}

}