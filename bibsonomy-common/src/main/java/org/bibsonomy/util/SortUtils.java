package org.bibsonomy.util;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.common.enums.SortKey;

/**
 * Convenience methods for sorting lists
 *
 * @author Dominik Benz
 * @version $Id$
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
	public static List<SortKey> parseSortKeys(String sortKeys) {
		ArrayList<SortKey> parsedSortKeys = new ArrayList<SortKey>();
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
	public static List<SortOrder> parseSortOrders(String sortOrders) {
		ArrayList<SortOrder> parsedSortOrders = new ArrayList<SortOrder>();
		if (sortOrders == null) {
			return parsedSortOrders;
		}
		for (String sortOrder : sortOrders.split("\\" + SORT_ORDER_DELIMITER)) {
			parsedSortOrders.add(EnumUtils.searchEnumByName(SortOrder.values(), sortOrder));
		}
		return parsedSortOrders;
	}	
}