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

	public SortCriteria() {
		this(SortKey.NONE, SortOrder.DESC);
	}

	public SortCriteria(SortKey sortKey, SortOrder sortOrder) {
		this.sortKey = sortKey;
		this.sortOrder = sortOrder;
	}

	public SortKey getSortKey() {
		return sortKey;
	}

	public SortOrder getSortOrder() {
		return sortOrder;
	}

}
