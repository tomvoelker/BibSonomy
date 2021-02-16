package org.bibsonomy.common;

import org.bibsonomy.common.enums.SortKey;
import org.bibsonomy.common.enums.SortOrder;

import java.util.Collections;
import java.util.List;

/**
 * @author kch
 */
public class SortCriteria {
	/** sort key */
	private SortKey sortKey;
	/** sort order */
	private SortOrder sortOrder;

	public SortCriteria() {
		this.sortKey = SortKey.NONE;
		this.sortOrder = SortOrder.DESC;
	}

	public SortCriteria(SortKey sortKey, SortOrder sortOrder) {
		this.sortKey = sortKey;
		this.sortOrder = sortOrder;
	}

	public SortCriteria(String key, String order) {
		this.sortKey = SortKey.getByName(key);
		this.sortOrder = SortOrder.getByName(order);
	}

	public static List<SortCriteria> singletonCriterium(SortKey key) {
		return Collections.singletonList(new SortCriteria(key, SortOrder.DESC));
	}

	public SortKey getSortKey() {
		return sortKey;
	}

	public void setSortKey(SortKey sortKey) {
		this.sortKey = sortKey;
	}

	public SortOrder getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}
}
