package org.bibsonomy.webapp.command.ajax;

import java.util.List;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.TagResourceViewCommand;


/**
 * @author bsc
 * @version $Id$
 */
public class UserSearchCommand extends TagResourceViewCommand {
	private String search;
	private int limit;
	private List<User> searchedUsers;
	
	/**
	 * @return the users
	 */
	public List<User> getSearchedUsers() {
		return this.searchedUsers;
	}
	/**
	 * @param users the users to set
	 */
	public void setSearchedUsers(List<User> users) {
		this.searchedUsers = users;
	}
	/**
	 * @return the search
	 */
	public String getSearch() {
		return this.search;
	}
	/**
	 * @param search the search to set
	 */
	public void setSearch(String search) {
		this.search = search;
	}
	/**
	 * @return the limit
	 */
	public int getLimit() {
		return this.limit;
	}
	/**
	 * @param limit the limit to set
	 */
	public void setLimit(int limit) {
		this.limit = (limit > 0 ? limit : 10);
	}
}
