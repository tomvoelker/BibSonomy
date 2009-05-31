package org.bibsonomy.webapp.command;

import java.util.List;

import org.bibsonomy.model.User;

/**
 * 
 * @author Christian Kramer
 * @version $Id$
 *
 */
public class FollowersViewCommand extends TagResourceViewCommand {
	private List<User> followersOfUser;
	private List<User> userIsFollowing;
	
	/**
	 * 
	 * @return all users which are following this user
	 */
	public List<User> getFollowersOfUser() {
		return this.followersOfUser;
	}
	
	/**
	 * 
	 * @param followersOfUser
	 */
	public void setFollowersOfUser(List<User> followersOfUser) {
		this.followersOfUser = followersOfUser;
	}
	
	/**
	 * 
	 * @return list of user which the user is following
	 */
	public List<User> getUserIsFollowing() {
		return this.userIsFollowing;
	}
	
	/**
	 * 
	 * @param userIsFollowing
	 */
	public void setUserIsFollowing(List<User> userIsFollowing) {
		this.userIsFollowing = userIsFollowing;
	}

	
}
