package org.bibsonomy.webapp.command;

import java.util.List;

import org.bibsonomy.model.User;

/**
 * Command for friends and offriend info
 *
 * @author dzo
 */
public class FriendsOverviewCommand extends MultiResourceViewCommand {

	private List<User> friends;
	private List<User> ofFriends;
	private int entriesPerPage = -1;

	/**
	 * @return the friends
	 */
	public List<User> getFriends() {
		return this.friends;
	}

	/**
	 * @param friends the friends to set
	 */
	public void setFriends(List<User> friends) {
		this.friends = friends;
	}

	/**
	 * @return the ofFriends
	 */
	public List<User> getOfFriends() {
		return this.ofFriends;
	}

	/**
	 * @param ofFriends the ofFriends to set
	 */
	public void setOfFriends(List<User> ofFriends) {
		this.ofFriends = ofFriends;
	}
	
	/**
	 * @return entries per page
	 */
	public int getEntriesPerPage() {
		if (this.entriesPerPage == -1) {
			// fallback to user settings, if not set explicitly before via url parameter
			this.entriesPerPage = this.getContext().getLoginUser().getSettings().getListItemcount(); 
		}
		return this.entriesPerPage;
	}

	/**
	 * @param entriesPerPage the entriesPerPage to set
	 */
	public void setEntriesPerPage(int entriesPerPage) {
		this.entriesPerPage = entriesPerPage;
	}
}
