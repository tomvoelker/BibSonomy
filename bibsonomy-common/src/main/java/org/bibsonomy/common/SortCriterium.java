package org.bibsonomy.common;

import org.bibsonomy.common.enums.SortKey;
import org.bibsonomy.common.enums.SortOrder;

import java.util.Collections;
import java.util.List;

/**
 * @author kch
 */
public class SortCriterium {
	/** sort key */
	private SortKey sortKey;
	/** sort order */
	private SortOrder sortOrder;

	public SortCriterium() {
		this.sortKey = SortKey.NONE;
		this.sortOrder = SortOrder.DESC;
	}

	public SortCriterium(SortKey sortKey, SortOrder sortOrder) {
		this.sortKey = sortKey;
		this.sortOrder = sortOrder;
	}

	public SortCriterium(String key, String order) {
		this.sortKey = SortKey.getByName(key);
		this.sortOrder = SortOrder.getByName(order);
	}

	public static List<SortCriterium> singletonCriterium(SortKey key) {
		return Collections.singletonList(new SortCriterium(key, SortOrder.DESC));
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
