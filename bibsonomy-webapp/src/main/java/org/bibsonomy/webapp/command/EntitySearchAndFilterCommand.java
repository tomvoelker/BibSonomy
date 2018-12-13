package org.bibsonomy.webapp.command;

import org.bibsonomy.common.enums.Prefix;
import org.bibsonomy.common.enums.SortOrder;

/**
 * class that other commands must extend for search and filtering entities
 *
 * @author dzo
 */
public abstract class EntitySearchAndFilterCommand extends ResourceViewCommand {
	private String search;

	private Prefix prefix;

	private SortOrder sortOrder = SortOrder.ASC;

	/**
	 * @return the search
	 */
	public String getSearch() {
		return search;
	}

	/**
	 * @param search the search to set
	 */
	public void setSearch(String search) {
		this.search = search;
	}

	/**
	 * @return the prefix
	 */
	public Prefix getPrefix() {
		return prefix;
	}

	/**
	 * @param prefix the prefix to set
	 */
	public void setPrefix(Prefix prefix) {
		this.prefix = prefix;
	}

	/**
	 * @return the sortOrder
	 */
	public SortOrder getSortOrder() {
		return sortOrder;
	}

	/**
	 * @param sortOrder the sortOrder to set
	 */
	public void setSortOrder(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}
}
