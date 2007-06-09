package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.LogicInterface;
import org.bibsonomy.database.Order;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.testutil.ModelUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jens Illig
 * @version $Id$
 */
public class RestDatabaseManagerTest extends AbstractDatabaseManagerTest {

	private static final Logger log = Logger.getLogger(RestDatabaseManagerTest.class);

	protected LogicInterface restDb;
	private List<Post<BibTex>> bibTexPostsList;
	private Set<Integer> alreadyFound;
	private List<String> taglist;
	private Set<String> tagSet;
	private List<String> taglistfriend;

	/** used for testing ordering */
	private long orderValue;
	private static final String TEST_USER_NAME = "jaeschke";
	private Set<String> testUserNameSet;
	private static final String TEST_REQUEST_USER_NAME = "jaeschke";
	private static final String TEST_REQUEST_HASH = "7d85e1092613fd7c91d6ba5dfcf4a044";

	@Before
	public void setUp() {
		super.setUp();
		this.restDb = RestDatabaseManager.getInstance();
		this.bibTexPostsList = null;
		this.alreadyFound = new HashSet<Integer>();
		this.orderValue = Long.MAX_VALUE; // DESC testing

		this.taglist = new LinkedList<String>();
		this.taglist.add("semantic");

		this.tagSet = ModelUtils.buildLowerCaseHashSet(this.taglist);

		this.taglistfriend = new LinkedList<String>();
		this.taglistfriend.add("DVD");

		this.testUserNameSet = new HashSet<String>(1, 1);
		this.testUserNameSet.add(TEST_USER_NAME);
	}

	@After
	public void tearDown() {
		super.tearDown();
		this.bibTexPostsList = null;
		this.taglist = null;
		this.taglistfriend = null;
	}

	private void assertList(final Set<String> checkUserNameOneOf, final Order checkOrder, final Set<String> checkTags, final String checkInterHash, final Set<Integer> mustBeInGroups, final Set<Integer> mustNotBeInGroups) {
		for (final Post post : this.bibTexPostsList) {
			assertTrue("contentid occured twice", alreadyFound.add(post.getContentId()));

			if (checkUserNameOneOf != null) {
				assertTrue("userName test with " + post.getUser().getName(), checkUserNameOneOf.contains(post.getUser().getName()));
			}
			if (checkOrder == Order.ADDED) {
				final long nextOrderValue = post.getDate().getTime();
				log.debug("date: " + nextOrderValue);
				assertTrue("order test", (this.orderValue >= nextOrderValue));
				this.orderValue = nextOrderValue;
			}
			/*
			FIXME: not tested, because no rank is in model (and probably should not be)
			if (checkOrder == Order.POPULAR) {
				int nextOrderValue = p.getResource().getRank();
				assertTrue("order test", (this.orderValue >= nextOrderValue));
				this.orderValue = nextOrderValue;
			}
			*/
			if (checkTags != null) {
				assertTrue("tag-test", ModelUtils.hasTags(post, checkTags));
			}
			if (checkInterHash != null) {
				assertEquals(post.getResource().getInterHash(), checkInterHash);
			}
			if ((mustBeInGroups != null) || (mustNotBeInGroups != null)) {
				assertTrue("group-test", ModelUtils.checkGroups(post, mustBeInGroups, mustNotBeInGroups));
			}
		}
	}

	@Test
	public void getPostsByTagName() {
		this.bibTexPostsList = restDb.getPosts(null, BibTex.class, GroupingEntity.ALL, "", this.taglist, "", null, 0, 5);
		assertEquals(5, this.bibTexPostsList.size());
		assertList(null, null, this.tagSet, null, null, null);
		this.bibTexPostsList = restDb.getPosts("", BibTex.class, GroupingEntity.ALL, "", this.taglist, null, null, 5, 9);
		assertEquals(4, this.bibTexPostsList.size());
		assertList(null, null, this.tagSet, null, null, null);
	}

	@Test
	public void getPostsByTagNameForUser() {
		this.bibTexPostsList = this.restDb.getPosts(TEST_USER_NAME, BibTex.class, GroupingEntity.USER, TEST_REQUEST_USER_NAME, taglist, "", null, 0, 10);
		assertEquals(10, this.bibTexPostsList.size());
		assertList(testUserNameSet, null, this.tagSet, null, null, null);
		this.bibTexPostsList = this.restDb.getPosts(TEST_USER_NAME, BibTex.class, GroupingEntity.USER, TEST_REQUEST_USER_NAME, taglist, null, null, 10, 19);
		assertEquals(9, this.bibTexPostsList.size());
		assertList(testUserNameSet, null, this.tagSet, null, null, null);
	}

	@Test
	public void getPostsByConceptForUser() {
		this.bibTexPostsList = this.restDb.getPosts(TEST_USER_NAME, BibTex.class, GroupingEntity.USER, TEST_REQUEST_USER_NAME, taglist, "", Order.ADDED, 0, 10);
		assertEquals(10, this.bibTexPostsList.size());
		assertList(testUserNameSet, Order.ADDED, null, null, null, null);
		this.bibTexPostsList = this.restDb.getPosts(TEST_USER_NAME, BibTex.class, GroupingEntity.USER, TEST_REQUEST_USER_NAME, taglist, null, Order.ADDED, 10, 19);
		assertEquals(9, this.bibTexPostsList.size());
		assertList(testUserNameSet, Order.ADDED, null, null, null, null);
	}

	@Test
	public void getPostsForUser() {
		this.bibTexPostsList = this.restDb.getPosts(TEST_USER_NAME, BibTex.class, GroupingEntity.USER, TEST_REQUEST_USER_NAME, null, "", null, 0, 10);
		assertEquals(10, this.bibTexPostsList.size());
		assertList(testUserNameSet, null, null, null, null, null);
		this.bibTexPostsList = this.restDb.getPosts(TEST_USER_NAME, BibTex.class, GroupingEntity.USER, TEST_REQUEST_USER_NAME, new ArrayList<String>(), null, null, 10, 19);
		assertEquals(9, this.bibTexPostsList.size());
		assertList(testUserNameSet, null, null, null, null, null);
	}

	@Test
	public void getPostsByHash() {
		this.bibTexPostsList = this.restDb.getPosts(TEST_USER_NAME, BibTex.class, GroupingEntity.ALL, "", new ArrayList<String>(), TEST_REQUEST_HASH, null, 0, 5);
		assertEquals(5, this.bibTexPostsList.size());
		assertList(null, null, null, TEST_REQUEST_HASH, null, null);
		this.bibTexPostsList = this.restDb.getPosts(TEST_USER_NAME, BibTex.class, GroupingEntity.ALL, "", null, TEST_REQUEST_HASH, null, 5, 6);
		assertEquals(1, this.bibTexPostsList.size());
		assertList(null, null, null, TEST_REQUEST_HASH, null, null);
		this.bibTexPostsList = this.restDb.getPosts(TEST_USER_NAME, BibTex.class, GroupingEntity.ALL, "", new ArrayList<String>(), TEST_REQUEST_HASH, null, 574, 134);
		assertEquals(0, this.bibTexPostsList.size());
	}

	@Test
	public void getPostsByHashForUser() {
		this.bibTexPostsList = this.restDb.getPosts(TEST_USER_NAME, BibTex.class, GroupingEntity.USER, TEST_REQUEST_USER_NAME, new ArrayList<String>(), TEST_REQUEST_HASH, null, 0, 19);
		assertEquals(1, this.bibTexPostsList.size());
		assertList(testUserNameSet, null, null, TEST_REQUEST_HASH, null, null);
	}

	@Test
	public void getPostsByViewable() {
		final Set<Integer> mustGroupIds = new HashSet<Integer>();
		final HashSet<String> usersInGroup = new HashSet<String>();
		usersInGroup.addAll(userDb.getUserNamesByGroupId(GroupID.GROUP_KDE.getId(), dbSession));
		mustGroupIds.add(GroupID.GROUP_KDE.getId());
		this.bibTexPostsList = this.restDb.getPosts(TEST_USER_NAME, BibTex.class, GroupingEntity.VIEWABLE, "kde", new ArrayList<String>(), "", Order.ADDED, 0, 3);
		assertEquals(3, this.bibTexPostsList.size());
		assertList(usersInGroup, Order.ADDED, null, null, mustGroupIds, null);
		this.bibTexPostsList = this.restDb.getPosts(TEST_USER_NAME, BibTex.class, GroupingEntity.VIEWABLE, "kde", new ArrayList<String>(), "", Order.ADDED, 3, 100);
		assertEquals(6, this.bibTexPostsList.size());
		assertList(usersInGroup, Order.ADDED, null, null, mustGroupIds, null);
	}

	@Test
	public void getPostsForUsersInGroup() {
		final HashSet<String> usersInGroup = new HashSet<String>();
		usersInGroup.addAll(userDb.getUserNamesByGroupId(GroupID.GROUP_KDE.getId(), dbSession));
		this.bibTexPostsList = this.restDb.getPosts(TEST_USER_NAME, BibTex.class, GroupingEntity.GROUP, "kde", null, "", null, 0, 10);
		assertEquals(10, this.bibTexPostsList.size());
		assertList(usersInGroup, null, null, null, null, null);
		this.bibTexPostsList = this.restDb.getPosts(TEST_USER_NAME, BibTex.class, GroupingEntity.GROUP, "kde", null, "", null, 10, 19);
		assertEquals(9, this.bibTexPostsList.size());
		assertList(usersInGroup, null, null, null, null, null);
	}

	@Test
	public void getPostsForGroupByTag() {
		final HashSet<String> usersInGroup = new HashSet<String>();
		usersInGroup.addAll(userDb.getUserNamesByGroupId(GroupID.GROUP_KDE.getId(), dbSession));
		this.bibTexPostsList = this.restDb.getPosts("", BibTex.class, GroupingEntity.GROUP, "kde", taglist, "", null, 0, 9);
		assertEquals(9, this.bibTexPostsList.size());
		assertList(usersInGroup, null, this.tagSet, null, null, null);
		this.bibTexPostsList = this.restDb.getPosts("", BibTex.class, GroupingEntity.GROUP, "kde", taglist, "", null, 9, 19);
		assertEquals(10, this.bibTexPostsList.size());
		assertList(usersInGroup, null, this.tagSet, null, null, null);
	}

	@Test
	public void getBibtexOfFriendByTags() {
		List<String> tags = Arrays.asList(new String[] { "java" });
		this.bibTexPostsList = this.restDb.getPosts("buzz", BibTex.class, GroupingEntity.FRIEND, "apo", tags, null, Order.ADDED, 0, 19);
		assertEquals(1, this.bibTexPostsList.size());
		final Set<String> tagsSet = new HashSet<String>();
		tagsSet.addAll(tags);
		final Set<String> userSet = new HashSet<String>();
		userSet.add("apo");
		final Set<Integer> mustGroupIds = new HashSet<Integer>();
		mustGroupIds.add(GroupID.GROUP_FRIENDS.getId());
		final HashSet<Integer> mustNotGroups = new HashSet<Integer>();
		mustNotGroups.add(GroupID.GROUP_PRIVATE.getId());
		mustNotGroups.add(GroupID.GROUP_PUBLIC.getId());
		assertList(userSet, Order.ADDED, tagsSet, null, mustGroupIds, mustNotGroups);

		this.bibTexPostsList = this.restDb.getPosts("jaeschke", BibTex.class, GroupingEntity.FRIEND, "apo", tags, null, null, 0, 19);
		assertEquals(0, this.bibTexPostsList.size());
	}

	@Test
	public void getBibtexOfFriendByUser() {
		this.bibTexPostsList = this.restDb.getPosts("buzz", BibTex.class, GroupingEntity.FRIEND, "apo", new ArrayList<String>(0), null, Order.ADDED, 0, 19);
		assertEquals(2, this.bibTexPostsList.size());
		final Set<Integer> mustGroupIds = new HashSet<Integer>();
		mustGroupIds.add(GroupID.GROUP_FRIENDS.getId());
		final HashSet<Integer> mustNotGroups = new HashSet<Integer>();
		mustNotGroups.add(GroupID.GROUP_PRIVATE.getId());
		mustNotGroups.add(GroupID.GROUP_PUBLIC.getId());
		final Set<String> userSet = new HashSet<String>();
		userSet.add("apo");
		assertList(userSet, Order.ADDED, null, null, mustGroupIds, mustNotGroups);

		this.bibTexPostsList = this.restDb.getPosts("jaeschke", BibTex.class, GroupingEntity.FRIEND, "apo", new ArrayList<String>(0), null, Order.ADDED, 0, 19);
		assertEquals(0, this.bibTexPostsList.size());
	}

	@Test
	public void getBibtexByFriends() {
		final HashSet<Integer> mustGroups = new HashSet<Integer>();
		mustGroups.add(GroupID.GROUP_FRIENDS.getId());
		final HashSet<Integer> mustNotGroups = new HashSet<Integer>();
		mustNotGroups.add(GroupID.GROUP_PRIVATE.getId());
		mustNotGroups.add(GroupID.GROUP_PUBLIC.getId());
		this.bibTexPostsList = this.restDb.getPosts("mwkuster", BibTex.class, GroupingEntity.FRIEND, null, null, null, Order.ADDED, 0, 19);
		assertEquals(19, this.bibTexPostsList.size());
		assertList(null, Order.ADDED, null, null, mustGroups, mustNotGroups);

		this.bibTexPostsList = this.restDb.getPosts("mwkuster", BibTex.class, GroupingEntity.FRIEND, null, null, null, Order.ADDED, 100, 200);
		assertEquals(10, this.bibTexPostsList.size());
		assertList(null, Order.ADDED, null, null, mustGroups, mustNotGroups);
	}

	@Test
	public void getPostsPopular() {
		this.bibTexPostsList = this.restDb.getPosts(TEST_USER_NAME, BibTex.class, GroupingEntity.ALL, "", null, null, Order.POPULAR, 0, 10);
		assertEquals(10, this.bibTexPostsList.size());
		assertList(null, Order.POPULAR, null, null, null, null);
		this.bibTexPostsList = this.restDb.getPosts(TEST_USER_NAME, BibTex.class, GroupingEntity.ALL, "", new ArrayList<String>(), null, Order.POPULAR, 10, 19);
		assertEquals(9, this.bibTexPostsList.size());
		assertList(null, Order.POPULAR, null, null, null, null);
	}

	@Test
	public void getPostsHome() {
		this.bibTexPostsList = this.restDb.getPosts(TEST_USER_NAME, BibTex.class, null, TEST_REQUEST_USER_NAME, taglist, null, null, 0, 19);
		assertEquals(15, this.bibTexPostsList.size());
		// TODO: test something
	}
}