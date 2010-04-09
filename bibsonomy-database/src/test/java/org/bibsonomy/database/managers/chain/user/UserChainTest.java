package org.bibsonomy.database.managers.chain.user;

import static org.junit.Assert.assertEquals;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.database.managers.chain.AbstractChainTest;
import org.bibsonomy.database.managers.chain.user.get.GetAllUsers;
import org.bibsonomy.database.managers.chain.user.get.GetFollowersOfUser;
import org.bibsonomy.database.managers.chain.user.get.GetFriendsOfUser;
import org.bibsonomy.database.managers.chain.user.get.GetRelatedUsersByTags;
import org.bibsonomy.database.managers.chain.user.get.GetRelatedUsersByUser;
import org.bibsonomy.database.managers.chain.user.get.GetUserFollowers;
import org.bibsonomy.database.managers.chain.user.get.GetUserFriends;
import org.bibsonomy.database.managers.chain.user.get.GetUsersByGroup;
import org.bibsonomy.database.params.UserParam;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the correct reaction of reach chain element of the user chain.
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public class UserChainTest extends AbstractChainTest {
	
	protected static UserChain userChain;
	
	/**
	 * sets up the chain
	 */
	@BeforeClass
	public static void setupChain() {
		userChain = new UserChain();
	}
	
	
	/**
	 * get all users
	 */
	@Test
	public void getAllUsers() {
		UserParam param = new UserParam();
		param.setGrouping(GroupingEntity.ALL);
		param.setTagIndex(null);
		userChain.getFirstElement().perform(param, this.dbSession, chainStatus);
		assertEquals(GetAllUsers.class, chainStatus.getChainElement().getClass());
	}
	
	/**
	 * get group members
	 */
	@Test
	public void getUsersByGroup() {
		UserParam param = new UserParam();
		param.setGrouping(GroupingEntity.GROUP);
		param.setRequestedGroupName("a_funny_groupname");
		userChain.getFirstElement().perform(param, this.dbSession, chainStatus);
		assertEquals(GetUsersByGroup.class, chainStatus.getChainElement().getClass());		
	}
	
	/**
	 * get related users by user
	 */
	@Test
	public void getRelatedUsersByUser() {
		UserParam param = new UserParam();
		param.setGrouping(GroupingEntity.USER);
		param.setRequestedUserName("a_funny_username");
		param.setUserRelation(UserRelation.FOLKRANK);
		userChain.getFirstElement().perform(param, this.dbSession, chainStatus);
		assertEquals(GetRelatedUsersByUser.class, chainStatus.getChainElement().getClass());		
	}	

	/**
	 * get related users by tags
	 */
	@Test
	public void getRelatedUsersByTags() {
		UserParam param = new UserParam();
		param.setGrouping(GroupingEntity.ALL);
		param.addTagName("a_funny_tag");
		param.setUserRelation(UserRelation.FOLKRANK);
		userChain.getFirstElement().perform(param, this.dbSession, chainStatus);
		assertEquals(GetRelatedUsersByTags.class, chainStatus.getChainElement().getClass());		
	}	
	
	/**
	 * get friends of user
	 */
	@Test
	public void getFriendsOfUser() {
		UserParam param = new UserParam();
		param.setGrouping(GroupingEntity.FRIEND);
		param.setUserName("a_funny_username");
		param.setUserRelation(UserRelation.FRIEND_OF);
		userChain.getFirstElement().perform(param, this.dbSession, chainStatus);
		assertEquals(GetFriendsOfUser.class, chainStatus.getChainElement().getClass());		
	}		
	
	/**
	 * get user friends
	 */
	@Test
	public void getUserFriends() {
		UserParam param = new UserParam();
		param.setGrouping(GroupingEntity.FRIEND);
		param.setUserName("a_funny_username");
		param.setUserRelation(UserRelation.OF_FRIEND);		
		userChain.getFirstElement().perform(param, this.dbSession, chainStatus);
		assertEquals(GetUserFriends.class, chainStatus.getChainElement().getClass());		
	}
	
	/**
	 * get user followers
	 */
	@Test
	public void getUserFollowers(){
		UserParam param = new UserParam();
		param.setGrouping(GroupingEntity.FOLLOWER);
		param.setUserName("test");
		param.setUserRelation(UserRelation.OF_FOLLOWER);
		userChain.getFirstElement().perform(param, this.dbSession, chainStatus);
		assertEquals(GetUserFollowers.class, chainStatus.getChainElement().getClass());
	}
	
	/**
	 * get followers of user
	 */
	@Test
	public void getFollowersOfUser(){
		UserParam param = new UserParam();
		param.setGrouping(GroupingEntity.FOLLOWER);
		param.setUserName("test");
		param.setUserRelation(UserRelation.FOLLOWER_OF);
		userChain.getFirstElement().perform(param, this.dbSession, chainStatus);
		assertEquals(GetFollowersOfUser.class, chainStatus.getChainElement().getClass());
	}
	
}