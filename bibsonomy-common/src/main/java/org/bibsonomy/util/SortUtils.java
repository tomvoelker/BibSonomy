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

import org.bibsonomy.common.SortCriterium;
import org.bibsonomy.common.enums.SortKey;
import org.bibsonomy.common.enums.SortOrder;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
			parsedSortKeys.add(EnumUtils.searchEnumByName(SortKey.values(), sortKey));
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

	public static List<SortCriterium> generateSortCriteriums(List<SortKey> sortKeys, List<SortOrder> sortOrders) {
		List<SortCriterium> sortCriteriums = new LinkedList<>();
		// Check, if any sort keys given
		if (sortKeys.isEmpty()) {
			return sortCriteriums;
		}
		// Check, if there is enough sort orders for each key
		if (sortOrders.size() >= sortKeys.size()) {
			// Create pair-wise sort criteriums
			Iterator<SortKey> sortKeysIt = sortKeys.iterator();
			Iterator<SortOrder> sortOrderIt = sortOrders.iterator();
			while (sortKeysIt.hasNext() && sortOrderIt.hasNext()) {
				sortCriteriums.add(new SortCriterium(sortKeysIt.next(), sortOrderIt.next()));
			}

		} else {
			// Not enough sort orders, take first sort order for all keys
			SortOrder sortOrder = sortOrders.get(0);
			for (SortKey sortKey : sortKeys) {
				sortCriteriums.add(new SortCriterium(sortKey, sortOrder));
			}
		}
		return sortCriteriums;
	}
}