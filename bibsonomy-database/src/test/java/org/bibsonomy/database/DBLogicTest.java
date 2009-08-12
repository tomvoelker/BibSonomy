package org.bibsonomy.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.AbstractDBLogicBase;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.testutil.ModelUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Jens Illig
 * @version $Id$
 */
public class DBLogicTest extends AbstractDBLogicBase {

	private static final Log log = LogFactory.getLog(DBLogicTest.class);

	private LogicInterface dbLogic;
	private List<Post<BibTex>> bibTexPostsList;
	private Set<Integer> alreadyFound;
	private List<String> taglist;
	private Set<String> tagSet;
	private List<String> taglistfriend;

	/** used for testing ordering */
	private long orderValue;
	private static final String TEST_USER_NAME    = "jaeschke";
	private static final String TEST_SPAMMER_NAME       = "testspammer2";
	private static final String TEST_SPAMMER_EMAIL      = "testspammer@bibsonomy.org";
	private static final String TEST_SPAMMER_ALGORITHM  = "testlogging";
	private static final int    TEST_SPAMMER_PREDICTION = 1;
	private static final double TEST_SPAMMER_CONFIDENCE = 0.23;
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
		final User user = new User();
		user.setName(userName);
		final DBLogic dbl = new DBLogic(user, this.getDbSessionFactory());
		return dbl;
	}

	@Override
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

	@Override
	@After
	public void tearDown() {
		super.tearDown();
		this.bibTexPostsList = null;
		this.taglist = null;
		this.taglistfriend = null;
	}

	private void assertList(final Set<String> checkUserNameOneOf, final Order checkOrder, final Set<String> checkTags, final String checkInterHash, final Set<Integer> mustBeInGroups, final Set<Integer> mustNotBeInGroups) {
		for (final Post<BibTex> post : this.bibTexPostsList) {
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

	/**
	 * tests getPostsByTagName
	 */
	@Test
	@Ignore
	public void getPostsByTagName() {
		LogicInterface anonymousAccess = this.getDbLogic(null);
		this.bibTexPostsList = anonymousAccess.getPosts(BibTex.class, GroupingEntity.ALL, "", this.taglist, "", null, null, 0, 5, null);
		assertEquals(5, this.bibTexPostsList.size());
		assertList(null, null, this.tagSet, null, null, null);
		anonymousAccess = getDbLogic("");
		this.bibTexPostsList = anonymousAccess.getPosts(BibTex.class, GroupingEntity.ALL, "", this.taglist, null, null, null, 5, 9, null);
		assertEquals(4, this.bibTexPostsList.size());
		assertList(null, null, this.tagSet, null, null, null);
	}

	/**
	 * tests getPostsByTagNameForUser
	 */
	@Test
	@Ignore
	public void getPostsByTagNameForUser() {
		this.bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.USER, TEST_REQUEST_USER_NAME, taglist, "", null, null, 0, 10, null);
		assertEquals(10, this.bibTexPostsList.size());
		assertList(testUserNameSet, null, this.tagSet, null, null, null);
		this.bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.USER, TEST_REQUEST_USER_NAME, taglist, null, null, null, 10, 19, null);
		assertEquals(9, this.bibTexPostsList.size());
		assertList(testUserNameSet, null, this.tagSet, null, null, null);
	}

	/**
	 * tests getPostsByConceptForUser
	 */
	@Test
	@Ignore
	public void getPostsByConceptForUser() {
		this.taglist = Arrays.asList(new String[] { "->researcher" });
		this.bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.USER, TEST_REQUEST_USER_NAME, taglist, "", Order.ADDED, null, 0, 2, null);
		assertEquals(2, this.bibTexPostsList.size());
		assertList(testUserNameSet, Order.ADDED, null, null, null, null);
		this.bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.USER, TEST_REQUEST_USER_NAME, taglist, null, Order.ADDED, null, 2, 10, null);
		assertEquals(1, this.bibTexPostsList.size());
		assertList(testUserNameSet, Order.ADDED, null, null, null, null);
	}

	/**
	 * tests getPostsForUser
	 */
	@Test
	@Ignore
	public void getPostsForUser() {
		this.bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.USER, TEST_REQUEST_USER_NAME, null, "", null, null, 0, 10, null);
		assertEquals(10, this.bibTexPostsList.size());
		assertList(testUserNameSet, null, null, null, null, null);
		this.bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.USER, TEST_REQUEST_USER_NAME, new ArrayList<String>(), null, null, null, 10, 19, null);
		assertEquals(9, this.bibTexPostsList.size());
		assertList(testUserNameSet, null, null, null, null, null);
	}

	/**
	 * tests getPostsByHash
	 */
	@Test
	@Ignore
	public void getPostsByHash() {
		this.bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.ALL, "", new ArrayList<String>(), TEST_REQUEST_HASH, null, null, 0, 5, null);
		assertEquals(5, this.bibTexPostsList.size());
		assertList(null, null, null, TEST_REQUEST_HASH, null, null);
		this.bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.ALL, "", null, TEST_REQUEST_HASH, null, null, 5, 6, null);
		assertEquals(1, this.bibTexPostsList.size());
		assertList(null, null, null, TEST_REQUEST_HASH, null, null);
		this.bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.ALL, "", new ArrayList<String>(), TEST_REQUEST_HASH, null, null, 574, 134, null);
		assertEquals(0, this.bibTexPostsList.size());
	}

	/**
	 * tests getPostsByHashForUser
	 */
	@Test
	@Ignore
	public void getPostsByHashForUser() {
		this.bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.USER, TEST_REQUEST_USER_NAME, new ArrayList<String>(), TEST_REQUEST_HASH, null, null, 0, 19, null);
		assertEquals(1, this.bibTexPostsList.size());
		assertEquals(1, this.bibTexPostsList.get(0).getGroups().size());
		assertEquals(null, this.bibTexPostsList.get(0).getResource().getDocuments());
		assertList(testUserNameSet, null, null, TEST_REQUEST_HASH, null, null);
	}

	/**
	 * tests getPostsByViewable
	 */
	@Test
	public void getPostsByViewable() {
		final Set<Integer> mustGroupIds = new HashSet<Integer>();
		final HashSet<String> usersInGroup = new HashSet<String>();
		usersInGroup.addAll( this.getUserNamesByGroupId( GroupID.KDE, this.dbSession) );
		mustGroupIds.add(GroupID.KDE.getId());
		this.bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.VIEWABLE, "kde", new ArrayList<String>(), "", Order.ADDED, null, 0, 3, null);
		assertEquals(0, this.bibTexPostsList.size());
		assertList(usersInGroup, Order.ADDED, null, null, mustGroupIds, null);
		this.bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.VIEWABLE, "kde", new ArrayList<String>(), "", Order.ADDED, null, 3, 100, null);
		assertEquals(0, this.bibTexPostsList.size());
		assertList(usersInGroup, Order.ADDED, null, null, mustGroupIds, null);
	}

	/**
	 * tests getPostsForUsersInGroup
	 */
	@Test
	@Ignore
	public void getPostsForUsersInGroup() {
		final HashSet<String> usersInGroup = new HashSet<String>();
		usersInGroup.addAll( this.getUserNamesByGroupId( GroupID.KDE, this.dbSession) );
		this.bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.GROUP, "kde", null, "", null, null, 0, 10, null);
		assertEquals(10, this.bibTexPostsList.size());
		assertList(usersInGroup, null, null, null, null, null);
		this.bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.GROUP, "kde", null, "", null, null, 10, 19, null);
		assertEquals(9, this.bibTexPostsList.size());
		assertList(usersInGroup, null, null, null, null, null);
	}

	/**
	 * tests getPostsForGroupByTag
	 */
	@Test
	@Ignore
	public void getPostsForGroupByTag() {
		final LogicInterface anonymousAccess = getDbLogic("");
		final HashSet<String> usersInGroup = new HashSet<String>();
		usersInGroup.addAll( this.getUserNamesByGroupId( GroupID.KDE, this.dbSession) );
		this.bibTexPostsList = anonymousAccess.getPosts(BibTex.class, GroupingEntity.GROUP, "kde", taglist, "", null, null, 0, 9, null);
		assertEquals(9, this.bibTexPostsList.size());
		assertList(usersInGroup, null, this.tagSet, null, null, null);
		this.bibTexPostsList = anonymousAccess.getPosts(BibTex.class, GroupingEntity.GROUP, "kde", taglist, "", null, null, 9, 19, null);
		assertEquals(10, this.bibTexPostsList.size());
		assertList(usersInGroup, null, this.tagSet, null, null, null);
	}

	/**
	 * tests getBibtexOfFriendByTags
	 */
	@Test
	@Ignore
	public void getBibtexOfFriendByTags() {
		final LogicInterface buzzsAccess = getDbLogic("buzz");
		final List<String> tags = Arrays.asList(new String[] { "java" });
		this.bibTexPostsList = buzzsAccess.getPosts(BibTex.class, GroupingEntity.FRIEND, "apo", tags, null, Order.ADDED, null, 0, 19, null);
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

		this.bibTexPostsList = getDbLogic().getPosts(BibTex.class, GroupingEntity.FRIEND, "apo", tags, null, null, null, 0, 19, null);
		assertEquals(0, this.bibTexPostsList.size());
	}

	/**
	 * tests getBibtexOfFriendByUser
	 */
	@Test
	@Ignore
	public void getBibtexOfFriendByUser() {
		final LogicInterface buzzsAccess = getDbLogic("buzz");
		this.bibTexPostsList = buzzsAccess.getPosts(BibTex.class, GroupingEntity.FRIEND, "apo", new ArrayList<String>(0), null, Order.ADDED, null, 0, 19, null);
		assertEquals(2, this.bibTexPostsList.size());
		final Set<Integer> mustGroupIds = new HashSet<Integer>();
		mustGroupIds.add(GroupID.FRIENDS.getId());
		final HashSet<Integer> mustNotGroups = new HashSet<Integer>();
		mustNotGroups.add(GroupID.PRIVATE.getId());
		mustNotGroups.add(GroupID.PUBLIC.getId());
		final Set<String> userSet = new HashSet<String>();
		userSet.add("apo");
		assertList(userSet, Order.ADDED, null, null, mustGroupIds, mustNotGroups);
		
		this.bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.FRIEND, "apo", new ArrayList<String>(0), null, Order.ADDED, null, 0, 19, null);
		assertEquals(0, this.bibTexPostsList.size());
	}

	/**
	 * tests getPosts with friends
	 */
	@Test
	@Ignore
	public void getBibtexByFriends() {
		final LogicInterface mwkustersAccess = getDbLogic("mwkuster");
		final HashSet<Integer> mustGroups = new HashSet<Integer>();
		mustGroups.add(GroupID.FRIENDS.getId());
		final HashSet<Integer> mustNotGroups = new HashSet<Integer>();
		mustNotGroups.add(GroupID.PRIVATE.getId());
		mustNotGroups.add(GroupID.PUBLIC.getId());
		this.bibTexPostsList = mwkustersAccess.getPosts(BibTex.class, GroupingEntity.FRIEND, null, null, null, Order.ADDED, null, 0, 19, null);
		assertEquals(19, this.bibTexPostsList.size());
		assertList(null, Order.ADDED, null, null, mustGroups, mustNotGroups);
		
		this.bibTexPostsList = mwkustersAccess.getPosts(BibTex.class, GroupingEntity.FRIEND, null, null, null, Order.ADDED, null, 100, 200, null);
		assertEquals(10, this.bibTexPostsList.size());
		assertList(null, Order.ADDED, null, null, mustGroups, mustNotGroups);
	}

	/**
	 * tests getPosts with popular
	 */
	@Test
	@Ignore
	public void getPostsPopular() {
		this.bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.ALL, "", null, null, Order.POPULAR, null, 0, 10, null);
		assertEquals(10, this.bibTexPostsList.size());
		assertList(null, Order.POPULAR, null, null, null, null);
		this.bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.ALL, "", new ArrayList<String>(), null, Order.POPULAR, null, 10, 19, null);
		assertEquals(9, this.bibTexPostsList.size());
		// FIXME: "contentid occured twice"
		// assertList(null, Order.POPULAR, null, null, null, null);
	}

	/**
	 * TODO fix this comment
	 */
	@Test
	@Ignore
	public void getPostsHome() {
		this.bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.ALL, TEST_REQUEST_USER_NAME, taglist, null, null, null, 0, 15, null);
		assertEquals(15, this.bibTexPostsList.size());
		// TODO: test something
	}

	/**
	 * tests concept store
	 */
	@Ignore
	// XXX: writes to the db (adapt to new test db)
	public void testConceptStore() {
		final Post<BibTex> post = ModelUtils.generatePost(BibTex.class);
		final Tag centerTag = new Tag("testCenterTag");

		final Tag superTag = new Tag("testSuperTag");
		superTag.setSubTags(Arrays.asList(new Tag[] {centerTag}));
		centerTag.setSuperTags(Arrays.asList(new Tag[] {superTag}));

		final Tag superSuperTag = new Tag("testSuperSuperTag");
		superSuperTag.setSuperTags(Arrays.asList(new Tag[] {superSuperTag}));
		superSuperTag.setSubTags(Arrays.asList(new Tag[] {superTag}));

		final Tag subTag = new Tag("testSubTag");
		centerTag.setSubTags(Arrays.asList(new Tag[] {subTag}));
		subTag.setSuperTags(Arrays.asList(new Tag[] {centerTag}));

		final String testUserName = this.getClass().getSimpleName();
		post.getTags().add(centerTag);
		post.getUser().setName(testUserName);

		final Group group = new Group();
		group.setGroupId(GroupID.PRIVATE.getId());
		group.setName("private");
		group.setDescription(null);
		post.setGroups(Collections.singleton(group));

		final LogicInterface testClassAccess = this.getDbLogic(testUserName);
		assertEquals(1, testClassAccess.getPosts(BibTex.class, GroupingEntity.USER, testUserName, Arrays.asList("->testSuperTag"), "", null, null, 0, 100, null).size());
		final List<Post<?>> posts = new LinkedList<Post<?>>();
		posts.add(post);
		testClassAccess.createPosts(posts);
		assertEquals(1, testClassAccess.getPosts(BibTex.class, GroupingEntity.USER, testUserName, Arrays.asList("->testSuperTag"), "", null, null, 0, 100, null).size());
		assertEquals(0, this.getDbLogic().getPosts(BibTex.class, GroupingEntity.USER, testUserName, Arrays.asList("->testSuperTag"), "", null, null, 0, 100, null).size());
	}

	/**
	 * We give a null document name, hence we should get a null document ... 
	 */
	@Test
	public void getDocumentNull() {
		final Document document = this.getDbLogic().getDocument(TEST_REQUEST_USER_NAME, TEST_REQUEST_HASH, null);
		assertNull(document);
	}

	/**
	 * A user wants to get his own document: should be possible.
	 */
	@Ignore
	// FIXME NullPointerException
	public void getDocumentOwn() {
		final String resourceHash = "4b020083ca0aca3d285569e5fbd0f5b7";
		final String documentFileName = "p16-gifford.pdf";
		final String documentHash = "0a7dbd07302ec230ca63bfaad4b94b42";
		final Document document = this.getDbLogic().getDocument(TEST_REQUEST_USER_NAME, resourceHash, documentFileName);
		assertNotNull(document);
		assertEquals(documentHash, document.getFileHash());
		assertEquals(documentFileName, document.getFileName());
	}

	/**
	 * A user wants to get another users document: should NOT be possible.
	 */
	@Test
	public void getDocumentNotOwn() {
		final String resourceHash = "4b020083ca0aca3d285569e5fbd0f5b7";
		final String documentFileName = "p16-gifford.pdf";
		final Document document = this.getDbLogic().getDocument("hotho", resourceHash, documentFileName);
		assertNull(document);
	}

	/**
	 * A user wants to get another users document: should be possible, if a group allows this.
	 */
	@Ignore
	// FIXME NullPointerException
	public void getDocumentNotOwnButSharedDocuments() {
		final String resourceHash = "dcf8eef77a3dfbc75f5e5ace931308a1";
		final String documentFileName = "interest.pdf";
		final String documentHash = "3ff32569c76b03ea1701e6ba436ffc63";
		final Document document = this.getDbLogic().getDocument("gromgull", resourceHash, documentFileName);
		assertNotNull(document);
		assertEquals(documentHash, document.getFileHash());
		assertEquals(documentFileName, document.getFileName());
	}

	/**
	 * tests getUsers by folkrank
	 */
	@Ignore
	public void testGetUsersByFolkrank(){
		List<String> tags = new ArrayList<String>();
		tags.add("web");
		List<User> user = this.getDbLogic().getUsers(null, null, null, tags, null, Order.FOLKRANK, null, null, 0, 20);
		assertEquals(20, user.size());
	}
	
	/**
	 * tests getUserDetails
	 */
	@Test
	public void getUserDetails() {
		// admin or the user himself has access to spam information
		LogicInterface dbl  = this.getDbLogic(TEST_SPAMMER_NAME);
		User spammer        = dbl.getUserDetails(TEST_SPAMMER_NAME);
		assertEquals(TEST_SPAMMER_NAME, spammer.getName());
		assertEquals(TEST_SPAMMER_ALGORITHM, spammer.getAlgorithm());
		assertEquals(TEST_SPAMMER_PREDICTION, spammer.getPrediction());
		assertEquals(TEST_SPAMMER_CONFIDENCE, spammer.getConfidence());
		assertEquals(TEST_SPAMMER_EMAIL, spammer.getEmail());
		
		// one can not read spam informations about other users
		dbl     = this.getDbLogic(TEST_USER_NAME);
		spammer = dbl.getUserDetails(TEST_SPAMMER_NAME);
		assertEquals(null, spammer.getAlgorithm());
		assertEquals(null, spammer.getPrediction());
		assertEquals(null, spammer.getConfidence());
		
	}
}