package org.bibsonomy.webapp.command;

import java.util.List;

import org.bibsonomy.model.User;

/**
 * @author Steffen Kress
 * @version $Id$
 */
public class FriendsResourceViewCommand extends TagResourceViewCommand {
	private List<User> userFriends;
	private List<User> friendsOfUser;
	/** for queries for specific kinds of users (e.g., friends) */
	private String userRelation;

	/**
	 * @param userFriends
	 */
	public void setUserFriends(List<User> userFriends) {
		this.userFriends = userFriends;
	}

	/**
	 * @param friendsOfUser
	 */
	public void setFriendsOfUser(List<User> friendsOfUser) {
		this.friendsOfUser = friendsOfUser;
		
	}

	/**
	 * @return friends of the user
	 */
	public List<User> getFriendsOfUser() {
		return friendsOfUser;
	}

	/**
	 * @return the users friends
	 */
	public List<User> getUserFriends() {
		return userFriends;
	}

	/**
	 * @return The relation the users to return have with the requested user.
	 */
	public String getUserRelation() {
		return this.userRelation;
	}

	/**
	 * @param userRelation
	 */
	public void setUserRelation(String userRelation) {
		this.userRelation = userRelation;
	}

}
