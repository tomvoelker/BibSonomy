package org.bibsonomy.database.managers.chain.user;

import static org.junit.Assert.assertEquals;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.database.managers.chain.AbstractChainTest;
import org.bibsonomy.database.managers.chain.user.get.GetAllUsers;
import org.bibsonomy.database.managers.chain.user.get.GetFriendsOfUser;
import org.bibsonomy.database.managers.chain.user.get.GetRelatedUsersByTags;
import org.bibsonomy.database.managers.chain.user.get.GetRelatedUsersByUser;
import org.bibsonomy.database.managers.chain.user.get.GetUserFriends;
import org.bibsonomy.database.managers.chain.user.get.GetUsersByGroup;
import org.bibsonomy.database.params.UserParam;
import org.junit.Test;

/**
 * Tests the correct reaction of reach chain element of the user chain.
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public class UserChainTest extends AbstractChainTest {

	/**
	 * get all users
	 */
	@Test
	public void getAllUsers() {
		UserParam param = new UserParam();
		param.setGrouping(GroupingEntity.ALL);
		param.setTagIndex(null);
		this.userChain.getFirstElement().perform(param, this.dbSession, this.chainStatus);
		assertEquals(GetAllUsers.class, this.chainStatus.getChainElement().getClass());
	}
	
	/**
	 * get group members
	 */
	@Test
	public void getUsersByGroup() {
		UserParam param = new UserParam();
		param.setGrouping(GroupingEntity.GROUP);
		param.setRequestedGroupName("a_funny_groupname");
		this.userChain.getFirstElement().perform(param, this.dbSession, this.chainStatus);
		assertEquals(GetUsersByGroup.class, this.chainStatus.getChainElement().getClass());		
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
		this.userChain.getFirstElement().perform(param, this.dbSession, this.chainStatus);
		assertEquals(GetRelatedUsersByUser.class, this.chainStatus.getChainElement().getClass());		
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
		this.userChain.getFirstElement().perform(param, this.dbSession, this.chainStatus);
		assertEquals(GetRelatedUsersByTags.class, this.chainStatus.getChainElement().getClass());		
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
		this.userChain.getFirstElement().perform(param, this.dbSession, this.chainStatus);
		assertEquals(GetFriendsOfUser.class, this.chainStatus.getChainElement().getClass());		
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
		this.userChain.getFirstElement().perform(param, this.dbSession, this.chainStatus);
		assertEquals(GetUserFriends.class, this.chainStatus.getChainElement().getClass());		
	}
	
}