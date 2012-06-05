package org.bibsonomy.database.managers;

import java.util.Collection;
import java.util.Collections;

import org.bibsonomy.database.common.params.beans.TagIndex;
import org.bibsonomy.database.params.ResourceParam;
import org.bibsonomy.database.systemstags.SystemTag;
import org.bibsonomy.model.Resource;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author dzo
 * @version $Id$
 * @param <R> 
 */
public abstract class PostDatabaseManagerTest<R extends Resource> extends AbstractDatabaseManagerTest {

	protected PostDatabaseManager<R, ? extends ResourceParam<R>> resourceDB;
	
	/**
	 * sets the resource manager to use
	 */
	@Before
	public abstract void setMananger();
	
	/**
	 * Test method for {@link org.bibsonomy.database.managers.PostDatabaseManager#getPostsFromInbox(java.lang.String, int, int, org.bibsonomy.database.common.DBSession)}.
	 */
	@Test
	public abstract void testGetPostsFromInbox();

	/**
	 * Test method for {@link org.bibsonomy.database.managers.PostDatabaseManager#getPostsFromInboxByHash(java.lang.String, java.lang.String, org.bibsonomy.database.common.DBSession)}.
	 */
	@Test
	public abstract void testGetPostsFromInboxByHash();

	/**
	 * Test method for {@link org.bibsonomy.database.managers.PostDatabaseManager#getPostsByConceptForGroup(java.lang.String, java.util.List, java.lang.String, java.util.List, int, int, java.util.Collection, org.bibsonomy.database.common.DBSession)}.
	 */
	@Test
	public abstract void testGetPostsByConceptForGroup();

	/**
	 * Test method for {@link org.bibsonomy.database.managers.PostDatabaseManager#getPostsByConceptForUser(java.lang.String, java.lang.String, java.util.List, java.util.List, boolean, int, int, java.util.Collection, org.bibsonomy.database.common.DBSession)}.
	 */
	@Test
	public abstract void testGetPostsByConceptForUser();

	/**
	 * Test method for {@link org.bibsonomy.database.managers.PostDatabaseManager#getPostsByTagNames(int, java.util.List, org.bibsonomy.model.enums.Order, int, int, org.bibsonomy.database.common.DBSession)}.
	 */
	@Test
	public abstract void testGetPostsByTagNames();

	/**
	 * Test method for {@link org.bibsonomy.database.managers.PostDatabaseManager#getPostsByTagNamesForUser(java.lang.String, java.lang.String, java.util.List, int, java.util.List, int, int, org.bibsonomy.common.enums.FilterEntity, java.util.Collection, org.bibsonomy.database.common.DBSession)}.
	 */
	@Test
	public abstract void testGetPostsByTagNamesForUser();

	/**
	 * Test method for {@link org.bibsonomy.database.managers.PostDatabaseManager#getPostsByTagNamesCount(java.util.List, int, org.bibsonomy.database.common.DBSession)}.
	 */
	@Test
	public abstract void testGetPostsByTagNamesCount();

	/**
	 * Test method for {@link org.bibsonomy.database.managers.PostDatabaseManager#getPostsByTagNamesForUserCount(java.lang.String, java.lang.String, java.util.List, java.util.List, org.bibsonomy.database.common.DBSession)}.
	 */
	@Test
	public void testGetPostsByTagNamesForUserCount() {
		// TODO: placeholder to execute the sql statement please add a test to all post resource dm's
		resourceDB.getPostsByTagNamesForUserCount("", "", Collections.singletonList(new TagIndex("google", 1)), Collections.<Integer>emptyList(), this.dbSession);
	}

	/**
	 * Test method for {@link org.bibsonomy.database.managers.PostDatabaseManager#getPostsByUserFriends(java.lang.String, org.bibsonomy.common.enums.HashID, int, int, java.util.Collection, org.bibsonomy.database.common.DBSession)}.
	 */
	@Test
	public abstract void testGetPostsByUserFriends();

	/**
	 * Test method for {@link org.bibsonomy.database.managers.PostDatabaseManager#getPostsPopular(int, int, int, org.bibsonomy.common.enums.HashID, org.bibsonomy.database.common.DBSession)}.
	 */
	@Test
	public abstract void testGetPostsPopular();

	/**
	 * Test method for {@link org.bibsonomy.database.managers.PostDatabaseManager#getPostPopularDays(int, org.bibsonomy.database.common.DBSession)}.
	 */
	@Test
	@Ignore
	public void testGetPostPopularDays() {
		// TODO: implement
	}

	/**
	 * Test method for {@link org.bibsonomy.database.managers.PostDatabaseManager#getPostsForHomepage(org.bibsonomy.common.enums.FilterEntity, int, int, java.util.Collection, org.bibsonomy.database.common.DBSession)}.
	 */
	@Test
	public abstract void testGetPostsForHomepage();

	/**
	 * Test method for {@link org.bibsonomy.database.managers.PostDatabaseManager#getPostsByHash(String, java.lang.String, org.bibsonomy.common.enums.HashID, int, Collection, int, int, org.bibsonomy.database.common.DBSession)}.
	 */
	@Test
	public abstract void testGetPostsByHash();

	/**
	 * Test method for {@link org.bibsonomy.database.managers.PostDatabaseManager#getPostsByHashCount(java.lang.String, org.bibsonomy.common.enums.HashID, org.bibsonomy.database.common.DBSession)}.
	 */
	@Test
	public abstract void testGetPostsByHashCount();

	/**
	 * Test method for {@link org.bibsonomy.database.managers.PostDatabaseManager#getPostsByHashForUser(java.lang.String, java.lang.String, java.lang.String, java.util.List, org.bibsonomy.common.enums.HashID, org.bibsonomy.database.common.DBSession)}.
	 */
	@Test
	public abstract void testGetPostsByHashForUser();
	
	/**
	 * Test method for {@link org.bibsonomy.database.managers.PostDatabaseManager#getPostsViewable(java.lang.String, java.lang.String, int, org.bibsonomy.common.enums.HashID, int, int, java.util.Collection, org.bibsonomy.database.common.DBSession)}.
	 */
	@Test
	public abstract void testGetPostsViewable();

	/**
	 * Test method for {@link org.bibsonomy.database.managers.PostDatabaseManager#getPostsViewableByTag(java.lang.String, java.lang.String, java.util.List, int, org.bibsonomy.common.enums.FilterEntity, int, int, java.util.Collection, org.bibsonomy.database.common.DBSession)}.
	 */
	@Test
	public void testGetPostsViewableByTag() {
		// TODO: placeholder to execute the sql statement please add a test to all post resource dm's
		resourceDB.getPostsViewableByTag("", "", Collections.singletonList(new TagIndex("google", 1)), TESTGROUP1_ID, null, 10, 0, Collections.<SystemTag>emptyList(), this.dbSession);
	}

	/**
	 * Test method for {@link org.bibsonomy.database.managers.PostDatabaseManager#getPostsForGroup(int, java.util.List, java.lang.String, org.bibsonomy.common.enums.HashID, org.bibsonomy.common.enums.FilterEntity, int, int, java.util.Collection, org.bibsonomy.database.common.DBSession)}.
	 */
	@Test
	public abstract void testGetPostsForGroup();

	/**
	 * Test method for {@link org.bibsonomy.database.managers.PostDatabaseManager#getPostsForGroupCount(java.lang.String, java.lang.String, int, java.util.List, org.bibsonomy.database.common.DBSession)}.
	 */
	@Test
	public abstract void testGetPostsForGroupCount();

	/**
	 * Test method for {@link org.bibsonomy.database.managers.PostDatabaseManager#getPostsForMyGroupPosts(java.lang.String, java.lang.String, int, int, java.util.List, java.util.Collection, org.bibsonomy.database.common.DBSession)}.
	 */
	@Test
	public void testGetPostsForMyGroupPosts() {
		// TODO: placeholder to execute the sql statement please add a test to all post resource dm's
		resourceDB.getPostsForMyGroupPosts("", "", 10, 0, Collections.singletonList(TESTGROUP1_ID), Collections.<SystemTag>emptyList(), this.dbSession);
	}

	/**
	 * Test method for {@link org.bibsonomy.database.managers.PostDatabaseManager#getPostsForMyGroupPostsByTag(java.lang.String, java.lang.String, java.util.List, int, int, java.util.List, java.util.Collection, org.bibsonomy.database.common.DBSession)}.
	 */
	@Test
	public void testGetPostsForMyGroupPostsByTag() {
		// TODO: placeholder to execute the sql statement please add a test to all post resource dm's
		resourceDB.getPostsForMyGroupPostsByTag("", "", Collections.singletonList(new TagIndex("google", 1)), 10, 0, Collections.singletonList(TESTGROUP1_ID), Collections.<SystemTag>emptyList(), this.dbSession);
	}

	/**
	 * Test method for {@link org.bibsonomy.database.managers.PostDatabaseManager#getPostsForGroupByTag(int, java.util.List, java.lang.String, java.util.List, org.bibsonomy.common.enums.FilterEntity, int, int, java.util.Collection, org.bibsonomy.database.common.DBSession)}.
	 */
	@Test
	public abstract void testGetPostsForGroupByTag();

	/**
	 * Test method for {@link org.bibsonomy.database.managers.PostDatabaseManager#getPostsForUser(java.lang.String, java.lang.String, org.bibsonomy.common.enums.HashID, int, java.util.List, org.bibsonomy.common.enums.FilterEntity, int, int, java.util.Collection, org.bibsonomy.database.common.DBSession)}.
	 */
	@Test
	public abstract void testGetPostsForUser();

	/**
	 * Test method for {@link org.bibsonomy.database.managers.PostDatabaseManager#getPostsForUserCount(java.lang.String, java.lang.String, int, java.util.List, org.bibsonomy.database.common.DBSession)}.
	 */
	@Test
	public abstract void testGetPostsForUserCount();

	/**
	 * Test method for {@link org.bibsonomy.database.managers.PostDatabaseManager#getPostsByFollowedUsers(java.lang.String, java.util.List, int, int, org.bibsonomy.database.common.DBSession)}.
	 */
	@Test
	public abstract void testGetPostsByFollowedUsers();

	/**
	 * Test method for {@link org.bibsonomy.database.managers.PostDatabaseManager#getGroupPostsCountByTag(java.lang.String, java.lang.String, java.util.List, java.util.List, org.bibsonomy.database.common.DBSession)}.
	 */
	@Test
	public void testGetGroupPostsCountByTag() {
		// TODO: placeholder to execute the sql statement please add a test to all post resource dm's
		resourceDB.getGroupPostsCountByTag("", "", Collections.singletonList(new TagIndex("google", 1)), Collections.singletonList(TESTGROUP1_ID), this.dbSession);
	}

	/**
	 * Test method for {@link org.bibsonomy.database.managers.PostDatabaseManager#getGroupPostsCount(java.lang.String, java.lang.String, java.util.List, org.bibsonomy.database.common.DBSession)}.
	 */
	@Test
	public void testGetGroupPostsCount() {
		// TODO: placeholder to execute the sql statement please add a test to all post resource dm's
		resourceDB.getGroupPostsCount("", "", Collections.singletonList(TESTGROUP1_ID), this.dbSession);
	}

	/**
	 * Test method for {@link org.bibsonomy.database.managers.PostDatabaseManager#getPostsFromBasketForUser(java.lang.String, int, int, org.bibsonomy.database.common.DBSession)}.
	 */
	@Test
	public abstract void testGetPostsFromBasketForUser();

	/**
	 * Test method for {@link org.bibsonomy.database.managers.PostDatabaseManager#getPostDetails(java.lang.String, java.lang.String, java.lang.String, java.util.List, org.bibsonomy.database.common.DBSession)}.
	 */
	@Test
	@Ignore
	public void testGetPostDetails() {
		// some other test methods are using the getPostDetailsMethod
	}

	/**
	 * Test method for {@link org.bibsonomy.database.managers.PostDatabaseManager#createPost(org.bibsonomy.model.Post, org.bibsonomy.database.common.DBSession)}.
	 */
	@Test
	public abstract void testCreatePost();

	/**
	 * Test method for {@link org.bibsonomy.database.managers.PostDatabaseManager#updatePost(org.bibsonomy.model.Post, java.lang.String, org.bibsonomy.common.enums.PostUpdateOperation, org.bibsonomy.database.common.DBSession)}.
	 */
	@Test
	public abstract void testUpdatePost();

	/**
	 * Test method for {@link org.bibsonomy.database.managers.PostDatabaseManager#deletePost(java.lang.String, java.lang.String, org.bibsonomy.database.common.DBSession)}.
	 */
	@Test
	public abstract void testDeletePost();

}
