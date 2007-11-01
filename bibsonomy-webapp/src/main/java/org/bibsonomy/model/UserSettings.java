/*
 * Created on 19.08.2007
 */
package org.bibsonomy.model;

/**
 * web-related settings of a user.
 * 
 * @author Jens Illig
 */
public class UserSettings {
	private Integer itemsPerPage;

	/**
	 * @return how many items the use want's to have on a single page's view-sublist
	 */
	public Integer getItemsPerPage() {
		return this.itemsPerPage;
	}

	/**
	 * @param itemsPerPage how many items the use want's to have on a single page's view-sublist
	 */
	public void setItemsPerPage(Integer itemsPerPage) {
		this.itemsPerPage = itemsPerPage;
	}
}
