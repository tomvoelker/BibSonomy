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
import org.bibsonomy.database.DBLogic;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.Order;
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

	private LogicInterface dbLogic;
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

	protected LogicInterface getDbLogic() {
		if (this.dbLogic == null) {
			this.dbLogic = this.getDbLogic(TEST_USER_NAME);
		}
		return this.dbLogic;
	}
		
	protected LogicInterface getDbLogic(final String userName) {
		final RestDatabaseManager restDbM = RestDatabaseManager.getInstance();
		restDbM.setDbSessionFactory(this.getDbSessionFactory());
		final DBLogic dbl = new DBLogic(userName, restDbM) {
		};
		return dbl;
	}
	
	@Before
	public void setUp() {
		super.setUp();

		this.bibTexPostsList = null;
		this.alreadyFound = new HashSet<Integer>();
		this.orderValue = Long.MAX_VALUE; // DESC testing

		this.taglist = new LinkedList<String>();
		this.taglist.add("semantic");
		
		this.tagSet = ModelUtils.buildLowerCaseHashSet(this.taglist);

		this.taglistfriend = new LinkedList<String>();
		this.taglistfriend.add("DVD");
		
		this.testUserNameSet = new HashSet<String>(1,1);
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
			log.debug("checking post with contentid " + post.getContentId());
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
		LogicInterface anonymousAccess = this.getDbLogic(null);
		this.bibTexPostsList = anonymousAccess.getPosts(BibTex.class, GroupingEntity.ALL, "", this.taglist, "", null, 0, 5);
		assertEquals(5, this.bibTexPostsList.size());
		assertList(null, null, this.tagSet, null, null, null);
		anonymousAccess = getDbLogic("");
		this.bibTexPostsList = anonymousAccess.getPosts(BibTex.class, GroupingEntity.ALL, "", this.taglist, null, null, 5, 9);
		assertEquals(4, this.bibTexPostsList.size());
		assertList(null, null, this.tagSet, null, null, null);
	}

	@Test
	public void getPostsByTagNameForUser() {
		this.bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.USER, TEST_REQUEST_USER_NAME, taglist, "", null, 0, 10);
		assertEquals(10, this.bibTexPostsList.size());
		assertList(testUserNameSet, null, this.tagSet, null, null, null);
		this.bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.USER, TEST_REQUEST_USER_NAME, taglist, null, null, 10, 19);
		assertEquals(9, this.bibTexPostsList.size());
		assertList(testUserNameSet, null, this.tagSet, null, null, null);
	}

	@Test
	public void getPostsByConceptForUser() {
		this.taglist = Arrays.asList(new String[] { "->researcher" });
		this.bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.USER, TEST_REQUEST_USER_NAME, taglist, "", Order.ADDED, 0, 2);
		assertEquals(2, this.bibTexPostsList.size());
		assertList(testUserNameSet, Order.ADDED, null, null, null, null);
		this.bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.USER, TEST_REQUEST_USER_NAME, taglist, null, Order.ADDED, 2, 10);
		assertEquals(2, this.bibTexPostsList.size());
		assertList(testUserNameSet, Order.ADDED, null, null, null, null);
	}

	@Test
	public void getPostsForUser() {
		this.bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.USER, TEST_REQUEST_USER_NAME, null, "", null, 0, 10);
		assertEquals(10, this.bibTexPostsList.size());
		assertList(testUserNameSet, null, null, null, null, null);
		this.bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.USER, TEST_REQUEST_USER_NAME, new ArrayList<String>(), null, null, 10, 19);
		assertEquals(9, this.bibTexPostsList.size());
		assertList(testUserNameSet, null, null, null, null, null);
	}

	@Test
	public void getPostsByHash() {
		this.bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.ALL, "", new ArrayList<String>(), TEST_REQUEST_HASH, null, 0, 5);
		assertEquals(5, this.bibTexPostsList.size());
		assertList(null, null, null, TEST_REQUEST_HASH, null, null);
		this.bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.ALL, "", null, TEST_REQUEST_HASH, null, 5, 6);
		assertEquals(1, this.bibTexPostsList.size());
		assertList(null, null, null, TEST_REQUEST_HASH, null, null);
		this.bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.ALL, "", new ArrayList<String>(), TEST_REQUEST_HASH, null, 574, 134);
		assertEquals(0, this.bibTexPostsList.size());
	}

	@Test
	public void getPostsByHashForUser() {
		this.bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.USER, TEST_REQUEST_USER_NAME, new ArrayList<String>(), TEST_REQUEST_HASH, null, 0, 19);
		assertEquals(1, this.bibTexPostsList.size());
		assertEquals(1, this.bibTexPostsList.get(0).getGroups().size());
		assertList(testUserNameSet, null, null, TEST_REQUEST_HASH, null, null);
	}

	@Test
	public void getPostsByViewable() {
		final Set<Integer> mustGroupIds = new HashSet<Integer>();
		final HashSet<String> usersInGroup = new HashSet<String>();
		usersInGroup.addAll( userDb.getUserNamesByGroupId( GroupID.KDE.getId(), this.dbSession) );
		mustGroupIds.add(GroupID.KDE.getId());
		this.bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.VIEWABLE, "kde", new ArrayList<String>(), "", Order.ADDED, 0, 3);
		assertEquals(3, this.bibTexPostsList.size());
		assertList(usersInGroup, Order.ADDED, null, null, mustGroupIds, null);
		this.bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.VIEWABLE, "kde", new ArrayList<String>(), "", Order.ADDED, 3, 100);
		assertEquals(6, this.bibTexPostsList.size());
		assertList(usersInGroup, Order.ADDED, null, null, mustGroupIds, null);
	}

	@Test
	public void getPostsForUsersInGroup() {
		final HashSet<String> usersInGroup = new HashSet<String>();
		usersInGroup.addAll( userDb.getUserNamesByGroupId( GroupID.KDE.getId(), this.dbSession) );
		this.bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.GROUP, "kde", null, "", null, 0, 10);
		assertEquals(10, this.bibTexPostsList.size());
		assertList(usersInGroup, null, null, null, null, null);
		this.bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.GROUP, "kde", null, "", null, 10, 19);
		assertEquals(9, this.bibTexPostsList.size());
		assertList(usersInGroup, null, null, null, null, null);
	}

	@Test
	public void getPostsForGroupByTag() {
		final LogicInterface anonymousAccess = getDbLogic("");
		final HashSet<String> usersInGroup = new HashSet<String>();
		usersInGroup.addAll( userDb.getUserNamesByGroupId( GroupID.KDE.getId(), this.dbSession) );
		this.bibTexPostsList = anonymousAccess.getPosts(BibTex.class, GroupingEntity.GROUP, "kde", taglist, "", null, 0, 9);
		assertEquals(9, this.bibTexPostsList.size());
		assertList(usersInGroup, null, this.tagSet, null, null, null);
		this.bibTexPostsList = anonymousAccess.getPosts(BibTex.class, GroupingEntity.GROUP, "kde", taglist, "", null, 9, 19);
		assertEquals(10, this.bibTexPostsList.size());
		assertList(usersInGroup, null, this.tagSet, null, null, null);
	}

	@Test
	public void getBibtexOfFriendByTags() {
		final LogicInterface buzzsAccess = getDbLogic("buzz");
		final List<String> tags = Arrays.asList(new String[] { "java" });
		this.bibTexPostsList = buzzsAccess.getPosts(BibTex.class, GroupingEntity.FRIEND, "apo", tags, null, Order.ADDED, 0, 19);
		assertEquals(1, this.bibTexPostsList.size());
		final Set<String> tagsSet = new HashSet<String>();
		tagsSet.addAll(tags);
		final Set<String> userSet = new HashSet<String>();
		userSet.add("apo");
		final Set<Integer> mustGroupIds = new HashSet<Integer>();
		mustGroupIds.add(GroupID.FRIENDS.getId());
		final HashSet<Integer> mustNotGroups = new HashSet<Integer>();
		mustNotGroups.add(GroupID.PRIVATE.getId());
		mustNotGroups.add(GroupID.PUBLIC.getId());
		assertList(userSet, Order.ADDED, tagsSet, null, mustGroupIds, mustNotGroups);

		this.bibTexPostsList = getDbLogic().getPosts(BibTex.class, GroupingEntity.FRIEND, "apo", tags, null, null, 0, 19);
		assertEquals(0, this.bibTexPostsList.size());
	}
	
	@Test
	public void getBibtexOfFriendByUser() {
		final LogicInterface buzzsAccess = getDbLogic("buzz");
		this.bibTexPostsList = buzzsAccess.getPosts(BibTex.class, GroupingEntity.FRIEND, "apo", new ArrayList<String>(0), null, Order.ADDED, 0, 19);
		assertEquals(2, this.bibTexPostsList.size());
		final Set<Integer> mustGroupIds = new HashSet<Integer>();
		mustGroupIds.add(GroupID.FRIENDS.getId());
		final HashSet<Integer> mustNotGroups = new HashSet<Integer>();
		mustNotGroups.add(GroupID.PRIVATE.getId());
		mustNotGroups.add(GroupID.PUBLIC.getId());
		final Set<String> userSet = new HashSet<String>();
		userSet.add("apo");
		assertList(userSet, Order.ADDED, null, null, mustGroupIds, mustNotGroups);
		
		this.bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.FRIEND, "apo", new ArrayList<String>(0), null, Order.ADDED, 0, 19);
		assertEquals(0, this.bibTexPostsList.size());
	}
	
	@Test
	public void getBibtexByFriends() {
		final LogicInterface mwkustersAccess = getDbLogic("mwkuster");
		final HashSet<Integer> mustGroups = new HashSet<Integer>();
		mustGroups.add(GroupID.FRIENDS.getId());
		final HashSet<Integer> mustNotGroups = new HashSet<Integer>();
		mustNotGroups.add(GroupID.PRIVATE.getId());
		mustNotGroups.add(GroupID.PUBLIC.getId());
		this.bibTexPostsList = mwkustersAccess.getPosts(BibTex.class, GroupingEntity.FRIEND, null, null, null, Order.ADDED, 0, 19);
		assertEquals(19, this.bibTexPostsList.size());
		assertList(null, Order.ADDED, null, null, mustGroups, mustNotGroups);
		
		this.bibTexPostsList = mwkustersAccess.getPosts(BibTex.class, GroupingEntity.FRIEND, null, null, null, Order.ADDED, 100, 200);
		assertEquals(10, this.bibTexPostsList.size());
		assertList(null, Order.ADDED, null, null, mustGroups, mustNotGroups);
	}

	@Test
	public void getPostsPopular() {
		this.bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.ALL, "", null, null, Order.POPULAR, 0, 10);
		assertEquals(10, this.bibTexPostsList.size());
		assertList(null, Order.POPULAR, null, null, null, null);
		this.bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.ALL, "", new ArrayList<String>(), null, Order.POPULAR, 10, 19);
		assertEquals(9, this.bibTexPostsList.size());
		assertList(null, Order.POPULAR, null, null, null, null);
	}

	@Test
	public void getPostsHome() {
		this.bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, null, TEST_REQUEST_USER_NAME, taglist, null, null, 0, 19);
		assertEquals(15, this.bibTexPostsList.size());
		// TODO: test something
	}
	
	@Test
	public void testConceptStore() {
		final Post<BibTex> post = ModelUtils.generatePost(BibTex.class);
		final Tag centerTag = new Tag();
		centerTag.setName("testCenterTag");
		
		final Tag superTag = new Tag();
		superTag.setName("testSuperTag");
		superTag.setSubTags(Arrays.asList(new Tag[] {centerTag}));
		centerTag.setSuperTags(Arrays.asList(new Tag[] {superTag}));
		
		final Tag superSuperTag = new Tag();
		superSuperTag.setName("testSuperSuperTag");
		superSuperTag.setSuperTags(Arrays.asList(new Tag[] {superSuperTag}));
		superSuperTag.setSubTags(Arrays.asList(new Tag[] {superTag}));
		
		final Tag subTag = new Tag();
		subTag.setName("testSubTag");
		centerTag.setSubTags(Arrays.asList(new Tag[] {subTag}));
		subTag.setSuperTags(Arrays.asList(new Tag[] {centerTag}));
		
		final String testUserName = this.getClass().getSimpleName();
		post.getTags().add(centerTag);
		post.getUser().setName(testUserName);
		
		final Group group = new Group();
		group.setGroupId(GroupID.PRIVATE.getId());
		group.setDescription(null);
		post.setGroups(Arrays.asList(new Group[] {group}));
		
		final LogicInterface testClassAccess = this.getDbLogic(testUserName);
		assertEquals( 0, testClassAccess.getPosts(BibTex.class, GroupingEntity.USER, testUserName, Arrays.asList("->testSuperTag"), "", null, 0, 100).size() );
		testClassAccess.storePost(post);
		assertEquals( 1, testClassAccess.getPosts(BibTex.class, GroupingEntity.USER, testUserName, Arrays.asList("->testSuperTag"), "", null, 0, 100).size() );
		assertEquals( 0, this.getDbLogic().getPosts(BibTex.class, GroupingEntity.USER, testUserName, Arrays.asList("->testSuperTag"), "", null, 0, 100).size() );
	}
}