package org.bibsonomy.database.managers.chain.user;

import org.bibsonomy.database.managers.chain.FirstListChainElement;
import org.bibsonomy.database.managers.chain.ListChainElement;
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
public class UserChain implements FirstListChainElement<User, UserParam> {

    private final ListChainElement<User, UserParam> getAllUsers;
    private final ListChainElement<User, UserParam> getFriendsOfUser;
    private final ListChainElement<User, UserParam> getRelatedUsersByTags;
    private final ListChainElement<User, UserParam> getRelatedUsersByUser;
    private final ListChainElement<User, UserParam> getUserFriends;
    private final ListChainElement<User, UserParam> getUsersByGroup;
    private final ListChainElement<User, UserParam> getFollowersOfUser;
    private final ListChainElement<User, UserParam> getUserFollowers;
    private final ListChainElement<User, UserParam> getPendingUsers;
    private final ListChainElement<User, UserParam> getPendingUserByUsername;
    private final ListChainElement<User, UserParam> getPendingUserByActivationCode;

    /**
     * Constructs the chain
     */
    public UserChain() {
	// intialize chain elements
	this.getAllUsers = new GetAllUsers();
	this.getFriendsOfUser = new GetFriendsOfUser();
	this.getRelatedUsersByTags = new GetRelatedUsersByTags();
	this.getRelatedUsersByUser = new GetRelatedUsersByUser();
	this.getUserFriends = new GetUserFriends();
	this.getUsersByGroup = new GetUsersByGroup();
	this.getFollowersOfUser = new GetFollowersOfUser();
	this.getUserFollowers = new GetUserFollowers();
	this.getPendingUsers = new GetPendingUsers();
	this.getPendingUserByUsername = new GetPendingUserByUsername();
	this.getPendingUserByActivationCode = new GetPendingUserByActivationCode();

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
	this.getPendingUserByUsername
		.setNext(this.getPendingUserByActivationCode);
    }

    @Override
    public ListChainElement<User, UserParam> getFirstElement() {
	return this.getUsersByGroup;
    }
}