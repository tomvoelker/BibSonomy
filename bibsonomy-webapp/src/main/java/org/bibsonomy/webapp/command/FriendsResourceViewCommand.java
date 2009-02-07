package org.bibsonomy.webapp.command;

import java.util.List;

import org.bibsonomy.model.User;

/**
 * @author Steffen
 * @version $Id$
 */
public class FriendsResourceViewCommand extends TagResourceViewCommand {
	/** the group whode resources are requested*/
	private ConceptsCommand concepts = new ConceptsCommand();
	private List<User> userFriends;
	private List<User> friendsOfUser;
	
	/**
	 * @return
	 */
	public ConceptsCommand getConcepts() {
		return this.concepts;
	}

	/**
	 * @param concepts
	 */
	public void setConcepts(ConceptsCommand concepts) {
		this.concepts = concepts;
	}

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
	
}
