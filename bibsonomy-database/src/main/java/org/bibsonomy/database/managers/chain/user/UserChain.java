package org.bibsonomy.database.managers.chain.user;

import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.database.managers.chain.FirstChainElement;
import org.bibsonomy.database.managers.chain.user.get.GetAllUsers;
import org.bibsonomy.database.managers.chain.user.get.GetFollowersOfUser;
import org.bibsonomy.database.managers.chain.user.get.GetFriendsOfUser;
import org.bibsonomy.database.managers.chain.user.get.GetPendingUserByActivationCode;
import org.bibsonomy.database.managers.chain.user.get.GetPendingUserByUsername;
import org.bibsonomy.database.managers.chain.user.get.GetPendingUsers;
import org.bibsonomy.database.managers.chain.user.get.GetRelatedUsersByTags;
import org.bibsonomy.database.managers.chain.user.get.GetRelatedUsersByUser;
import org.bibsonomy.database.managers.chain.user.get.GetUserFollowers;
import org.bibsonomy.database.managers.chain.user.get.GetUserFriends;
import org.bibsonomy.database.managers.chain.user.get.GetUsersByGroup;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.model.User;

/**
 * Chain for user queries
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public class UserChain implements FirstChainElement<User, UserParam> {

	private final ChainElement<User, UserParam> getAllUsers;
	private final ChainElement<User, UserParam> getFriendsOfUser;
	private final ChainElement<User, UserParam> getRelatedUsersByTags;
	private final ChainElement<User, UserParam> getRelatedUsersByUser;
	private final ChainElement<User, UserParam> getUserFriends;
	private final ChainElement<User, UserParam> getUsersByGroup;
	private final ChainElement<User, UserParam> getFollowersOfUser;
	private final ChainElement<User, UserParam> getUserFollowers;
    private final ChainElement<User, UserParam> getPendingUsers;
    private final ChainElement<User, UserParam> getPendingUserByUsername;
    private final ChainElement<User, UserParam> getPendingUserByActivationCode;

	/**
	 * Constructs the chain
	 */
	public UserChain() {
		// intialize chain elements
		this.getAllUsers 				     = new GetAllUsers();
		this.getFriendsOfUser			     = new GetFriendsOfUser();
		this.getRelatedUsersByTags 		     = new GetRelatedUsersByTags();
		this.getRelatedUsersByUser 		     = new GetRelatedUsersByUser();
		this.getUserFriends				     = new GetUserFriends();
		this.getUsersByGroup			     = new GetUsersByGroup();
		this.getFollowersOfUser			     = new GetFollowersOfUser();
		this.getUserFollowers			     = new GetUserFollowers();
        this.getPendingUsers                 = new GetPendingUsers();
        this.getPendingUserByUsername  = new GetPendingUserByUsername();
        this.getPendingUserByActivationCode  = new GetPendingUserByActivationCode();
        
		// set order of chain elements
		this.getUsersByGroup.setNext(this.getRelatedUsersByUser);
		this.getRelatedUsersByUser.setNext(this.getRelatedUsersByTags);
		this.getRelatedUsersByTags.setNext(this.getFriendsOfUser);
		this.getFriendsOfUser.setNext(this.getUserFriends);
		this.getUserFriends.setNext(this.getFollowersOfUser);
		this.getFollowersOfUser.setNext(this.getUserFollowers);
		this.getUserFollowers.setNext(this.getAllUsers);
        this.getAllUsers.setNext(this.getPendingUsers);
        this.getPendingUsers.setNext(this.getPendingUserByUsername);
        this.getPendingUserByUsername.setNext(this.getPendingUserByActivationCode);
	}

	public ChainElement<User, UserParam> getFirstElement() {
		return this.getUsersByGroup;
	}
}