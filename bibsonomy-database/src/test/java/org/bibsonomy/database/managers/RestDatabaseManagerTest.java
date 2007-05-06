package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.LogicInterface;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jens Illig
 * @version $Id$
 */
public class RestDatabaseManagerTest extends AbstractDatabaseManagerTest {
	
	protected LogicInterface restDb;
	private List<Post<BibTex>> bibTexPostsList;
	private List<String> taglist;
	private List<String> taglistfriend;
	private static final String TEST_USER_NAME = "jaeschke";

	@Before
	public void setUp() {
		super.setUp();
		this.restDb = RestDatabaseManager.getInstance();
		this.bibTexPostsList = null;
		this.taglist = new LinkedList<String>();
		this.taglistfriend = new LinkedList<String>();
		this.taglist.add("semantic");
		this.taglistfriend.add("DVD");
	}

	@After
	public void tearDown() {
		super.tearDown();
		this.bibTexPostsList = null;
		this.taglist = null;
		this.taglistfriend = null;
	}

	@Test
	public void getPostsByTagName() {
		// ByTagName
		this.bibTexPostsList = restDb.getPosts(TEST_USER_NAME, BibTex.class, GroupingEntity.ALL, "jaeschke", taglist, null, false, false, 0, 10);
		assertEquals(10, this.bibTexPostsList.size());
	}

	@Test
	public void getPostsByTagNameForUser() {
		// ByTagNameForUser
		this.bibTexPostsList = this.restDb.getPosts(TEST_USER_NAME, BibTex.class, GroupingEntity.USER, "jaeschke", taglist, null, false, false, 0, 19);
		assertEquals(19, this.bibTexPostsList.size());
	}

	@Test
	public void getPostsByConceptForUser() {
		// ByConceptForUser
		this.bibTexPostsList = this.restDb.getPosts(TEST_USER_NAME, BibTex.class, GroupingEntity.USER, "jaeschke", taglist, null, false, true, 0, 19);
		assertEquals(19, this.bibTexPostsList.size());
	}

	@Test
	public void getPostsForUser() {
		// ForUser
		this.bibTexPostsList = this.restDb.getPosts(TEST_USER_NAME, BibTex.class, GroupingEntity.USER, "jaeschke", null, null, false, false, 0, 19);
		assertEquals(19, this.bibTexPostsList.size());
	}

	@Test
	public void getPostsByHash() {
		// ByHash
		this.bibTexPostsList = this.restDb.getPosts(TEST_USER_NAME, BibTex.class, GroupingEntity.ALL, "jaeschke", null, "7d85e1092613fd7c91d6ba5dfcf4a044", false, false, 0, 19);
		assertEquals(6, this.bibTexPostsList.size());
	}

	@Test
	public void getPostsByHashForUser() {
		// ByHashForUser
		// FIXME geht noch nicht
		this.bibTexPostsList = this.restDb.getPosts(TEST_USER_NAME, BibTex.class, GroupingEntity.USER, "jaeschke", null, "7d85e1092613fd7c91d6ba5dfcf4a044", false, false, 0, 19);
		assertEquals(1, this.bibTexPostsList.size());
	}

	@Test
	public void getPostsByViewable() {
		// ByViewable
		this.bibTexPostsList = this.restDb.getPosts(TEST_USER_NAME, BibTex.class, GroupingEntity.VIEWABLE, "jaeschke", null, null, false, false, 0, 19);
		assertEquals(19, this.bibTexPostsList.size());
	}

	@Test
	public void getPostsForGroup() {
		// ForGroup
		this.bibTexPostsList = this.restDb.getPosts(TEST_USER_NAME, BibTex.class, GroupingEntity.GROUP, "kde", null, null, false, false, 0, 19);
		assertEquals(19, this.bibTexPostsList.size());
	}

	@Test
	public void getPostsForGroupByTag() {
		// ForGroupByTag
		this.bibTexPostsList = this.restDb.getPosts(TEST_USER_NAME, BibTex.class, GroupingEntity.GROUP, "kde", taglist, null, false, false, 0, 19);
		assertEquals(19, this.bibTexPostsList.size());
	}

	@Test
	public void getPostsByFriendName() {
		// ByFriendName
		this.bibTexPostsList = this.restDb.getPosts(TEST_USER_NAME, BibTex.class, GroupingEntity.FRIEND, "ralfm", null, null, false, false, 0, 19);
		assertEquals(0, this.bibTexPostsList.size());
	}

	@Test
	public void getPostsByFriendNameAndTag() {
		// ByFriendNameAndTag
		this.bibTexPostsList = this.restDb.getPosts(TEST_USER_NAME, BibTex.class, GroupingEntity.FRIEND, "ralfm", taglistfriend, null, false, false, 0, 19);
		assertEquals(0, this.bibTexPostsList.size());
	}

	@Test
	public void getPostsPopular() {
		// Popular
		this.bibTexPostsList = this.restDb.getPosts(TEST_USER_NAME, BibTex.class, GroupingEntity.ALL, "jaeschke", taglist, null, true, false, 0, 19);
		assertEquals(91, this.bibTexPostsList.size());
	}

	@Test
	public void getPostsHome() {
		// Home
		this.bibTexPostsList = this.restDb.getPosts(TEST_USER_NAME, BibTex.class, null, "jaeschke", taglist, null, false, false, 0, 19);
		assertEquals(15, this.bibTexPostsList.size());
	}
}