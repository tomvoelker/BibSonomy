package org.bibsonomy.database.managers.chain.user;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.managers.chain.Chain;
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
import org.bibsonomy.database.managers.chain.user.get.GetUsersBySearch;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.model.User;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the correct reaction of reach chain element of the user chain.
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public class UserChainTest extends AbstractDatabaseManagerTest {
	
	protected static Chain<List<User>, UserParam> userChain;
	
	/**
	 * sets up the chain
	 */
	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setupChain() {
		userChain = (Chain<List<User>, UserParam>) testDatabaseContext.getBean("userChain");
	}
	
	/**
	 * get all users
	 */
	@Test
	public void getAllUsers() {
		final UserParam param = new UserParam();
		param.setGrouping(GroupingEntity.ALL);
		param.setTagIndex(null);
		assertEquals(GetAllUsers.class, userChain.getChainElement(param).getClass());
	}
	
	/**
	 * get group members
	 */
	@Test
	public void getUsersByGroup() {
		final UserParam param = new UserParam();
		param.setGrouping(GroupingEntity.GROUP);
		param.setRequestedGroupName("a_funny_groupname");
		assertEquals(GetUsersByGroup.class, userChain.getChainElement(param).getClass());		
	}
	
	/**
	 * get related users by user
	 */
	@Test
	public void getRelatedUsersByUser() {
		final UserParam param = new UserParam();
		param.setGrouping(GroupingEntity.USER);
		param.setRequestedUserName("a_funny_username");
		param.setUserRelation(UserRelation.FOLKRANK);
		assertEquals(GetRelatedUsersByUser.class, userChain.getChainElement(param).getClass());		
	}	

	/**
	 * get related users by tags
	 */
	@Test
	public void getRelatedUsersByTags() {
		final UserParam param = new UserParam();
		param.setGrouping(GroupingEntity.ALL);
		param.addTagName("a_funny_tag");
		param.setUserRelation(UserRelation.FOLKRANK);
		assertEquals(GetRelatedUsersByTags.class, userChain.getChainElement(param).getClass());		
	}	
	
	/**
	 * get friends of user
	 */
	@Test
	public void getFriendsOfUser() {
		final UserParam param = new UserParam();
		param.setGrouping(GroupingEntity.FRIEND);
		param.setUserName("a_funny_username");
		param.setUserRelation(UserRelation.FRIEND_OF);
		assertEquals(GetFriendsOfUser.class, userChain.getChainElement(param).getClass());		
	}		
	
	/**
	 * get user friends
	 */
	@Test
	public void getUserFriends() {
		final UserParam param = new UserParam();
		param.setGrouping(GroupingEntity.FRIEND);
		param.setUserName("a_funny_username");
		param.setUserRelation(UserRelation.OF_FRIEND);
		assertEquals(GetUserFriends.class, userChain.getChainElement(param).getClass());		
	}
	
	/**
	 * get user followers
	 */
	@Test
	public void getUserFollowers(){
		final UserParam param = new UserParam();
		param.setGrouping(GroupingEntity.FOLLOWER);
		param.setUserName("test");
		param.setUserRelation(UserRelation.OF_FOLLOWER);
		assertEquals(GetUserFollowers.class, userChain.getChainElement(param).getClass());
	}
	
    /**
     * get all pending users
     **/
    @Test
    public void getPendingUsers(){
        final UserParam param = new UserParam();
        param.setGrouping(GroupingEntity.PENDING);
        assertEquals(GetPendingUsers.class, userChain.getChainElement(param).getClass());
    }
    
    /**
     * get all pending users
     **/
    @Test
    public void getPendingUserByActivationCode(){
        final UserParam param = new UserParam();
        param.setGrouping(GroupingEntity.PENDING);
        param.setSearch("00000000000000000000000000000000");
        assertEquals(GetPendingUserByActivationCode.class, userChain.getChainElement(param).getClass());
    }
    
    /**
     * get pending users by username
     **/
    @Test
    public void getPendingUserByUsername(){
        final UserParam param = new UserParam();
        param.setGrouping(GroupingEntity.PENDING);
        param.setRequestedGroupName("activationtestuser1");
        assertEquals(GetPendingUserByUsername.class, userChain.getChainElement(param).getClass());
    }
	
	/**
	 * get followers of user
	 */
	@Test
	public void getFollowersOfUser(){
		final UserParam param = new UserParam();
		param.setGrouping(GroupingEntity.FOLLOWER);
		param.setUserName("test");
		param.setUserRelation(UserRelation.FOLLOWER_OF);
		assertEquals(GetFollowersOfUser.class, userChain.getChainElement(param).getClass());
	}
	
	/**
	 * get users by searchString 
	 */
	@Test
	public void getUsersBySearch(){
		final UserParam param = new UserParam();
		param.setSearch("testuser");
		param.setLimit(10);
		param.setGrouping(GroupingEntity.USER);
		assertEquals(GetUsersBySearch.class, userChain.getChainElement(param).getClass());
	}
	
}