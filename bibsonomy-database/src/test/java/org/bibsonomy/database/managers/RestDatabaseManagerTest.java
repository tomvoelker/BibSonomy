/*
 * Created on 01.05.2007
 */
package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.LogicInterface;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RestDatabaseManagerTest extends AbstractDatabaseManagerTest {
	
	protected LogicInterface restDb;
	private List<Post<Resource>> postsList;
	private List<String> taglist;
	private List<String> taglistfriend;

	@Before
	public void setUp() {
		super.setUp();
		this.restDb = RestDatabaseManager.getInstance();
		this.postsList = null;
		this.taglist = new LinkedList<String>();
		this.taglistfriend = new LinkedList<String>();
		this.taglist.add("semantic");
		this.taglistfriend.add("DVD");
	}

	@After
	public void tearDown() {
		super.tearDown();
		this.postsList = null;
		this.taglist = null;
		this.taglistfriend = null;
	}

	@Test
	public void performByTagName() {
		// ByTagName
		this.postsList = restDb.getPosts("jaeschke", Resource.class, GroupingEntity.ALL, "jaeschke", taglist, null, false, false, 0, 10);
		assertEquals(10, this.postsList.size());
	}

	@Test
	public void performByTagNameForUser() {
		// ByTagNameForUser
		this.postsList = this.restDb.getPosts("jaeschke", Resource.class, GroupingEntity.USER, "jaeschke", taglist, null, false, false, 0, 19);
		assertEquals(19, this.postsList.size());
	}

	@Test
	public void performByConceptForUser() {
		// ByConceptForUser
		this.postsList = this.restDb.getPosts("jaeschke", Resource.class, GroupingEntity.USER, "jaeschke", taglist, null, false, true, 0, 19);
		assertEquals(19, this.postsList.size());
	}

	@Test
	public void performForUser() {
		// ForUser
		this.postsList = this.restDb.getPosts("jaeschke", Resource.class, GroupingEntity.USER, "jaeschke", null, null, false, false, 0, 19);
		assertEquals(19, this.postsList.size());
	}

	@Test
	public void performByHash() {
		// ByHash
		this.postsList = this.restDb.getPosts("jaeschke", Resource.class, GroupingEntity.ALL, "jaeschke", null, "7d85e1092613fd7c91d6ba5dfcf4a044", false, false, 0, 19);
		assertEquals(6, this.postsList.size());
	}

	@Test
	public void performByHashForUser() {
		// ByHashForUser
		// FIXME geht noch nicht
		this.postsList = this.restDb.getPosts("jaeschke", Resource.class, GroupingEntity.USER, "jaeschke", null, "7d85e1092613fd7c91d6ba5dfcf4a044", false, false, 0, 19);
		assertEquals(1, this.postsList.size());
	}

	@Test
	public void performByViewable() {
		// ByViewable
		this.postsList = this.restDb.getPosts("jaeschke", Resource.class, GroupingEntity.VIEWABLE, "jaeschke", null, null, false, false, 0, 19);
		assertEquals(19, this.postsList.size());
	}

	@Test
	public void performForGroup() {
		// ForGroup
		this.postsList = this.restDb.getPosts("jaeschke", Resource.class, GroupingEntity.GROUP, "kde", null, null, false, false, 0, 19);
		assertEquals(19, this.postsList.size());
	}

	@Test
	public void performForGroupByTag() {
		// ForGroupByTag
		this.postsList = this.restDb.getPosts("jaeschke", Resource.class, GroupingEntity.GROUP, "kde", taglist, null, false, false, 0, 19);
		assertEquals(19, this.postsList.size());
	}

	@Test
	public void performByFriendName() {
		// ByFriendName
		this.postsList = this.restDb.getPosts("jaeschke", Resource.class, GroupingEntity.FRIEND, "ralfm", null, null, false, false, 0, 19);
		assertEquals(0, this.postsList.size());
	}

	@Test
	public void performByFriendNameAndTag() {
		// ByFriendNameAndTag
		this.postsList = this.restDb.getPosts("jaeschke", Resource.class, GroupingEntity.FRIEND, "ralfm", taglistfriend, null, false, false, 0, 19);
		assertEquals(0, this.postsList.size());
	}

	@Test
	public void performPopular() {
		// Popular
		this.postsList = this.restDb.getPosts("jaeschke", Resource.class, GroupingEntity.ALL, "jaeschke", taglist, null, true, false, 0, 19);
		assertEquals(91, this.postsList.size());
	}

	@Test
	public void performHome() {
		// Home
		this.postsList = this.restDb.getPosts("jaeschke", Resource.class, null, "jaeschke", taglist, null, false, false, 0, 19);
		assertEquals(15, this.postsList.size());
	}
}
