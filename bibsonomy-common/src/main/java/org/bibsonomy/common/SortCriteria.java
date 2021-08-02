package org.bibsonomy.common;

import org.bibsonomy.common.enums.SortKey;
import org.bibsonomy.common.enums.SortOrder;

/**
 * @author kch
 */
public class SortCriteria {
	/** sort key */
	private final SortKey sortKey;
	/** sort order */
	private final SortOrder sortOrder;

	/**
	 * default constructor
	 * sets the key to {@link SortKey#NONE} and the order to {@link SortOrder#DESC}
	 */
	public SortCriteria() {
		this(SortKey.NONE, SortOrder.DESC);
	}

	/**
	 * constructor to create a sort critria from scratch
	 * @param sortKey
	 * @param sortOrder
	 */
	public SortCriteria(final SortKey sortKey, final SortOrder sortOrder) {
		this.sortKey = sortKey;
		this.sortOrder = sortOrder;
	}

	/**
	 * @return the sortKey
	 */
	public SortKey getSortKey() {
		return sortKey;
	}

	/**
	 * @return the sortOrder
	 */
	public SortOrder getSortOrder() {
		return sortOrder;
	}
}
