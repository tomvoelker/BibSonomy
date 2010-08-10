package org.bibsonomy.database;

import static org.bibsonomy.testutil.Assert.assertTagsByName;
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

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.common.enums.ProfilePrivlevel;
import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.database.managers.AbstractDBLogicBase;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.testutil.ModelUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Jens Illig
 * @version $Id$
 */
public class DBLogicTest extends AbstractDBLogicBase {
	private static final String TEST_USER_NAME = "jaeschke";
	private static final String TEST_SPAMMER_NAME = "testspammer2";
	private static final String TEST_SPAMMER_EMAIL = "testspammer@bibsonomy.org";
	private static final String TEST_SPAMMER_ALGORITHM = "testlogging";
	private static final int    TEST_SPAMMER_PREDICTION = 1;
	private static final double TEST_SPAMMER_CONFIDENCE = 0.23;
	
	private static final String TEST_REQUEST_USER_NAME = "jaeschke";
	private static final String TEST_REQUEST_HASH = "7d85e1092613fd7c91d6ba5dfcf4a044";
	
	private static final List<String> DEFAULT_TAG_LIST = new LinkedList<String>(Arrays.asList("semantic"));
	private static final Set<String> DEFAULT_TAG_SET = new HashSet<String>(DEFAULT_TAG_LIST);
	
	private static final Set<String> DEFAULT_USERNAME_SET = new HashSet<String>(Arrays.asList(TEST_USER_NAME));
	
	protected LogicInterface getDbLogic() {		
		return this.getDbLogic(TEST_USER_NAME);
	}

	protected LogicInterface getDbLogic(final String userName) {
		final User user = new User();
		user.setName(userName);
		return new DBLogic(user, this.getDbSessionFactory());
	}
	
	private static void assertList(final List<Post<BibTex>> posts, final Set<String> checkUserNameOneOf, final Order checkOrder, final Set<String> checkTags, final String checkInterHash, final Set<Integer> mustBeInGroups, final Set<Integer> mustNotBeInGroups) {
		final Set<Integer> alreadyFound = new HashSet<Integer>();
		long orderValue = Long.MAX_VALUE;
		
		for (final Post<? extends Resource> post : posts) {
			assertTrue("contentid occured twice", alreadyFound.add(post.getContentId()));

			if (checkUserNameOneOf != null) {
				assertTrue("userName test with " + post.getUser().getName(), checkUserNameOneOf.contains(post.getUser().getName()));
			}
			if (checkOrder == Order.ADDED) {
				final long nextOrderValue = post.getDate().getTime();
				assertTrue("order test", (orderValue >= nextOrderValue));
				orderValue = nextOrderValue;
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
		List<Post<BibTex>> bibTexPostsList = anonymousAccess.getPosts(BibTex.class, GroupingEntity.ALL, "", DEFAULT_TAG_LIST, "", null, null, 0, 5, null);
		assertEquals(5, bibTexPostsList.size());
		assertList(bibTexPostsList, null, null, DEFAULT_TAG_SET, null, null, null);
		
		anonymousAccess = getDbLogic("");
		bibTexPostsList = anonymousAccess.getPosts(BibTex.class, GroupingEntity.ALL, "", DEFAULT_TAG_LIST, null, null, null, 5, 9, null);
		assertEquals(4, bibTexPostsList.size());
		assertList(bibTexPostsList, null, null, DEFAULT_TAG_SET, null, null, null);
	}

	/**
	 * tests getPostsByConceptForUser
	 */
	@Test
	@Ignore
	public void getPostsByConceptForUser() {
		final List<String> taglist = Arrays.asList("->researcher");
		List<Post<BibTex>> bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.USER, TEST_REQUEST_USER_NAME, taglist, "", Order.ADDED, null, 0, 2, null);
		assertEquals(2, bibTexPostsList.size());
		assertList(bibTexPostsList, DEFAULT_USERNAME_SET, Order.ADDED, null, null, null, null);
		
		bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.USER, TEST_REQUEST_USER_NAME, taglist, null, Order.ADDED, null, 2, 10, null);
		assertEquals(1, bibTexPostsList.size());
		assertList(bibTexPostsList, DEFAULT_USERNAME_SET, Order.ADDED, null, null, null, null);
	}

	/**
	 * tests getPostsForUser
	 */
	@Test
	@Ignore
	public void getPostsForUser() {
		List<Post<BibTex>> bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.USER, TEST_REQUEST_USER_NAME, null, "", null, null, 0, 10, null);
		assertEquals(10, bibTexPostsList.size());
		assertList(bibTexPostsList, DEFAULT_USERNAME_SET, null, null, null, null, null);
		
		bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.USER, TEST_REQUEST_USER_NAME, new ArrayList<String>(), null, null, null, 10, 19, null);
		assertEquals(9, bibTexPostsList.size());
		assertList(bibTexPostsList, DEFAULT_USERNAME_SET, null, null, null, null, null);
	}

	/**
	 * tests getPostsByHash on Bibtex entries
	 */
	@Test
	@Ignore
	public void getPostsByHashBibtex() {
		List<Post<BibTex>> listBibtex = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.ALL, "", new ArrayList<String>(), "d9eea4aa159d70ecfabafa0c91bbc9f0", null, null, 0, 5, null);
		assertEquals(1, listBibtex.size());
		assertEquals(1, listBibtex.get(0).getGroups().size());
		for (Group g : listBibtex.get(0).getGroups()){
			assertEquals("public", g.getName());
		}
		
		List<Post<Bookmark>> listBookmark = this.getDbLogic().getPosts(Bookmark.class, GroupingEntity.ALL, "", new ArrayList<String>(), "85ab919107e4cc79b345e996b3c0b097", null, null, 0, 5, null);
		assertEquals(1, listBookmark.size());
		assertEquals(1, listBookmark.get(0).getGroups().size());
		for (Group g : listBookmark.get(0).getGroups()){
			assertEquals("public", g.getName());
		}
	}

	/**
	 * tests getPostsByHashForUser
	 */
	@Test
	@Ignore
	public void getPostsByHashForUser() {
		List<Post<BibTex>> bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.USER, TEST_REQUEST_USER_NAME, new ArrayList<String>(), TEST_REQUEST_HASH, null, null, 0, 19, null);
		assertEquals(1, bibTexPostsList.size());
		assertEquals(1, bibTexPostsList.get(0).getGroups().size());
		assertNull(bibTexPostsList.get(0).getResource().getDocuments());
		assertList(bibTexPostsList, DEFAULT_USERNAME_SET, null, null, TEST_REQUEST_HASH, null, null);
	}

	/**
	 * tests getPostsByViewable
	 */
	@Test
	public void getPostsByViewable() {
		final Set<Integer> mustGroupIds = new HashSet<Integer>();
		final Set<String> usersInGroup = new HashSet<String>();
		usersInGroup.addAll(getUserNamesByGroupId(GroupID.KDE, this.dbSession));
		mustGroupIds.add(GroupID.KDE.getId());
		
		List<Post<BibTex>> bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.VIEWABLE, "kde", new ArrayList<String>(), "", Order.ADDED, null, 0, 3, null);
		assertEquals(0, bibTexPostsList.size());
		assertList(bibTexPostsList, usersInGroup, Order.ADDED, null, null, mustGroupIds, null);
		
		bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.VIEWABLE, "kde", new ArrayList<String>(), "", Order.ADDED, null, 3, 100, null);
		assertEquals(0, bibTexPostsList.size());
		assertList(bibTexPostsList, usersInGroup, Order.ADDED, null, null, mustGroupIds, null);
	}

	/**
	 * tests getPostsForUsersInGroup
	 */
	@Test
	@Ignore
	public void getPostsForUsersInGroup() {
		final Set<String> usersInGroup = new HashSet<String>();
		usersInGroup.addAll(getUserNamesByGroupId(GroupID.KDE, this.dbSession) );
		List<Post<BibTex>> bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.GROUP, "kde", null, "", null, null, 0, 10, null);
		assertEquals(10, bibTexPostsList.size());
		assertList(bibTexPostsList, usersInGroup, null, null, null, null, null);
		
		bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.GROUP, "kde", null, "", null, null, 10, 19, null);
		assertEquals(9, bibTexPostsList.size());
		assertList(bibTexPostsList, usersInGroup, null, null, null, null, null);
	}

	/**
	 * tests getPostsForGroupByTag
	 */
	@Test
	@Ignore
	public void getPostsForGroupByTag() {
		final LogicInterface anonymousAccess = getDbLogic("");
		final Set<String> usersInGroup = new HashSet<String>();
		usersInGroup.addAll(getUserNamesByGroupId(GroupID.KDE, this.dbSession) );
		
		List<Post<BibTex>> bibTexPostsList = anonymousAccess.getPosts(BibTex.class, GroupingEntity.GROUP, "kde", DEFAULT_TAG_LIST, "", null, null, 0, 9, null);
		assertEquals(9, bibTexPostsList.size());
		assertList(bibTexPostsList, usersInGroup, null, DEFAULT_TAG_SET, null, null, null);
		
		bibTexPostsList = anonymousAccess.getPosts(BibTex.class, GroupingEntity.GROUP, "kde", DEFAULT_TAG_LIST, "", null, null, 9, 19, null);
		assertEquals(10, bibTexPostsList.size());
		assertList(bibTexPostsList, usersInGroup, null, DEFAULT_TAG_SET, null, null, null);
	}

	/**
	 * tests getBibtexOfFriendByTags
	 */
	@Test
	@Ignore
	public void getBibtexOfFriendByTags() {
		final LogicInterface buzzsAccess = getDbLogic("buzz");
		final List<String> tags = Arrays.asList("java");
		List<Post<BibTex>> bibTexPostsList = buzzsAccess.getPosts(BibTex.class, GroupingEntity.FRIEND, "apo", tags, null, Order.ADDED, null, 0, 19, null);
		assertEquals(1, bibTexPostsList.size());
		final Set<String> tagsSet = new HashSet<String>();
		tagsSet.addAll(tags);
		final Set<String> userSet = new HashSet<String>();
		userSet.add("apo");
		
		final Set<Integer> mustGroupIds = new HashSet<Integer>();
		mustGroupIds.add(FRIENDS_GROUP_ID);
		
		final Set<Integer> mustNotGroups = new HashSet<Integer>();
		mustNotGroups.add(PRIVATE_GROUP_ID);
		mustNotGroups.add(PUBLIC_GROUP_ID);
		assertList(bibTexPostsList, userSet, Order.ADDED, tagsSet, null, mustGroupIds, mustNotGroups);

		bibTexPostsList = getDbLogic().getPosts(BibTex.class, GroupingEntity.FRIEND, "apo", tags, null, null, null, 0, 19, null);
		assertEquals(0, bibTexPostsList.size());
	}

	/**
	 * tests getBibtexOfFriendByUser
	 */
	@Test
	@Ignore
	public void getBibtexOfFriendByUser() {
		final LogicInterface buzzsAccess = getDbLogic("buzz");
		final Set<Integer> mustGroupIds = new HashSet<Integer>();
		mustGroupIds.add(FRIENDS_GROUP_ID);
		final Set<Integer> mustNotGroups = new HashSet<Integer>();
		mustNotGroups.add(PRIVATE_GROUP_ID);
		mustNotGroups.add(PUBLIC_GROUP_ID);
		final Set<String> userSet = new HashSet<String>();
		userSet.add("apo");
		
		List<Post<BibTex>> bibTexPostsList = buzzsAccess.getPosts(BibTex.class, GroupingEntity.FRIEND, "apo", new ArrayList<String>(0), null, Order.ADDED, null, 0, 19, null);
		assertEquals(2, bibTexPostsList.size());
		assertList(bibTexPostsList, userSet, Order.ADDED, null, null, mustGroupIds, mustNotGroups);
		
		bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.FRIEND, "apo", new ArrayList<String>(0), null, Order.ADDED, null, 0, 19, null);
		assertEquals(0, bibTexPostsList.size());
	}

	/**
	 * tests getPosts with friends
	 */
	@Test
	@Ignore
	public void getBibtexByFriends() {
		final LogicInterface mwkustersAccess = getDbLogic("mwkuster");
		final Set<Integer> mustGroups = new HashSet<Integer>();
		mustGroups.add(FRIENDS_GROUP_ID);
		final Set<Integer> mustNotGroups = new HashSet<Integer>();
		mustNotGroups.add(PRIVATE_GROUP_ID);
		mustNotGroups.add(PUBLIC_GROUP_ID);
		
		List<Post<BibTex>> bibTexPostsList = mwkustersAccess.getPosts(BibTex.class, GroupingEntity.FRIEND, null, null, null, Order.ADDED, null, 0, 19, null);
		assertEquals(19, bibTexPostsList.size());
		assertList(bibTexPostsList, null, Order.ADDED, null, null, mustGroups, mustNotGroups);
		
		bibTexPostsList = mwkustersAccess.getPosts(BibTex.class, GroupingEntity.FRIEND, null, null, null, Order.ADDED, null, 100, 200, null);
		assertEquals(10, bibTexPostsList.size());
		assertList(bibTexPostsList, null, Order.ADDED, null, null, mustGroups, mustNotGroups);
	}

	/**
	 * tests getPosts with popular
	 */
	@Test
	@Ignore
	public void getPostsPopular() {
		List<Post<BibTex>> bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.ALL, "", null, null, Order.POPULAR, null, 0, 10, null);
		assertEquals(10, bibTexPostsList.size());
		assertList(bibTexPostsList, null, Order.POPULAR, null, null, null, null);
		
		bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.ALL, "", new ArrayList<String>(), null, Order.POPULAR, null, 10, 19, null);
		assertEquals(9, bibTexPostsList.size());
		assertList(bibTexPostsList, null, Order.POPULAR, null, null, null, null);
	}

	/**
	 * TODO improve documentation
	 */
	@Test
	@Ignore
	public void getPostsHome() {
		List<Post<BibTex>> bibTexPostsList = this.getDbLogic().getPosts(BibTex.class, GroupingEntity.ALL, TEST_REQUEST_USER_NAME, DEFAULT_TAG_LIST, null, null, null, 0, 15, null);
		assertEquals(15, bibTexPostsList.size());
	}

	/**
	 * tests concept store
	 */
    @Test
	@Ignore
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
		group.setGroupId(PRIVATE_GROUP_ID);
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
	@Test
	public void getDocumentOwn() {
		final String resourceHash = "b77ddd8087ad8856d77c740c8dc2864a";
		final String documentFileName = "testdocument_1.pdf";
		final Document document = this.getDbLogic("testuser1").getDocument("testuser1", resourceHash, documentFileName);
		assertNotNull(document);
		assertEquals("00000000000000000000000000000000", document.getFileHash());
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
	 * A user wants to get another user's document: should be possible, if a group allows this.
	 */
	@Test
	public void getDocumentNotOwnButSharedDocuments() {
		final String resourceHash = "b77ddd8087ad8856d77c740c8dc2864a";
		final String documentFileName = "testdocument_1.pdf";
		final String documentHash = "00000000000000000000000000000000";
		final Document document = this.getDbLogic("testuser2").getDocument("testuser1", resourceHash, documentFileName);
		assertNotNull(document);
		assertEquals(documentHash, document.getFileHash());
		assertEquals(documentFileName, document.getFileName());
	}

	/**
	 * tests getUsers by folkrank
	 */
	@Ignore
	@Test
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
		assertNull(spammer.getAlgorithm());
		assertNull(spammer.getPrediction());
		assertNull(spammer.getConfidence());
	}
	
	/**
	 * tests {@link DBLogic#getUserDetails(String)}
	 */
	@Test
	public void getUserDetailsForUserNotInDB() {
		final LogicInterface dbl  = this.getDbLogic(TEST_USER_NAME);
		final User user = dbl.getUserDetails("thisuserdoesntexistindb");
		assertNull(user.getName()); // user unknown => user's name must be null
	}
	
	/**
	 * tests the profile privacy settings
	 */
	@Test
	public void userProfilePrivacy() {
		final String username1 = "testuser3";
		final String username2 = "testuser1";
		final String username3 = "testuser2";
		final LogicInterface logic  = this.getDbLogic(username1);
		final LogicInterface logic2 = this.getDbLogic(username2);
		final LogicInterface logic3 = this.getDbLogic(username3);
		
		final User user = logic.getUserDetails(username1);
		assertNotNull(user.getRealname()); // see my own name
		
		user.getSettings().setProfilePrivlevel(ProfilePrivlevel.PRIVATE);
		logic.updateUser(user, UserUpdateOperation.UPDATE_CORE);
		final User userafterUpdatePrivate = logic2.getUserDetails(username1);
		assertNull(userafterUpdatePrivate.getRealname());
		
		user.getSettings().setProfilePrivlevel(ProfilePrivlevel.PUBLIC);
		logic.updateUser(user, UserUpdateOperation.UPDATE_CORE);
		final User userAfterUpdatePublic = logic2.getUserDetails(username1);
		assertNotNull(userAfterUpdatePublic.getRealname());
		
		user.getSettings().setProfilePrivlevel(ProfilePrivlevel.FRIENDS);
		logic.updateUser(user, UserUpdateOperation.UPDATE_CORE);
		final User user1AfterUpdateFriends = logic2.getUserDetails(username1);
		assertNull(user1AfterUpdateFriends.getRealname()); // testuser3 has no friends
		
		final User user2 = logic3.getUserDetails(username3);
		user2.getSettings().setProfilePrivlevel(ProfilePrivlevel.FRIENDS);
		logic3.updateUser(user2, UserUpdateOperation.UPDATE_CORE);
		
		final User user2AfterUpdateFriends = logic2.getUserDetails(username3);
		assertNotNull(user2AfterUpdateFriends.getRealname()); // testuser1 is friend of testuser2
	}
	
	/**
	 * tests {@link PostUpdateOperation#UPDATE_TAGS} for a publication
	 */
	@Test
	public void testPostUpdateTagOnlyOperationPublication() {
		final LogicInterface dbl = this.getDbLogic(TEST_REQUEST_USER_NAME);
		/*
		 *  create a post (a publication)
		 */
		final Post<BibTex> post = ModelUtils.generatePost(BibTex.class);
		
		// add tags
		ModelUtils.addToTagSet(post.getTags(), "testCenterTag", "secondTag");
		
		post.getUser().setName(TEST_REQUEST_USER_NAME);

		final List<Post<?>> posts = new LinkedList<Post<?>>();
		posts.add(post);
		final List<String> createPosts = dbl.createPosts(posts);
		assertEquals(1, createPosts.size());
		
		Post<? extends Resource> savedPost = null;
		try {
			savedPost = dbl.getPostDetails(createPosts.get(0), TEST_REQUEST_USER_NAME);
		} catch (Exception ex1) {
			// ignore
		}
		assertNotNull(savedPost);
		
		// get the contentId if more than tags were updated the contentId changes
		final int contentId = savedPost.getContentId();
		
		// abstract
		final String expectedBibtexAbstract = ((BibTex) savedPost.getResource()).getAbstract();
		/*
		 * modify the post; add and remove one tag
		 */
		ModelUtils.addToTagSet(savedPost.getTags(), "newTag");
		savedPost.getTags().remove(new Tag("testCenterTag"));
		
		final BibTex bibtex = (BibTex) savedPost.getResource();
		bibtex.setAbstract("PostUpdateOperation.UPDATE_TAGS");
		
		/*
		 * update the post
		 */
		final List<Post<?>> updates = new LinkedList<Post<?>>();
		updates.add(savedPost);
		
		final List<String> updatedPosts = dbl.updatePosts(updates, PostUpdateOperation.UPDATE_TAGS);
		assertEquals(1, updatedPosts.size());
		
		/*
		 * check if only tags were updated
		 */
		Post<? extends Resource> updatedResource = null;
		try {
			updatedResource = dbl.getPostDetails(createPosts.get(0), TEST_REQUEST_USER_NAME);
		} catch (Exception ex) {
			// ignore
		}
		assertNotNull(updatedResource);
		
		// check content id
		assertEquals(contentId, updatedResource.getContentId());
		
		// check tags
		assertTagsByName(ModelUtils.getTagSet("org.bibsonomy.testutil.ModelUtils", "hurz", "secondTag", "newTag"), updatedResource.getTags());
		
		// check changed
		assertEquals(expectedBibtexAbstract, ((BibTex) updatedResource.getResource()).getAbstract());
	}
	
	/**
	 * tests {@link PostUpdateOperation#UPDATE_TAGS} for a bookmark
	 */
	@Test
	public void testPostUpdateTagOnlyOperationBookmark() {
		final LogicInterface dbl = this.getDbLogic(TEST_REQUEST_USER_NAME);
		
		/*
		 *  create a post (a bookmark)
		 */
		final Post<Bookmark> post = ModelUtils.generatePost(Bookmark.class);
		
		// add tags
		ModelUtils.addToTagSet(post.getTags(), "testCenterTag", "secondTag");
		
		post.getUser().setName(TEST_REQUEST_USER_NAME);
		final Bookmark bookmarkB = post.getResource();
		final String url = bookmarkB.getUrl();
		
		final List<Post<?>> posts = new LinkedList<Post<?>>();
		posts.add(post);
		final List<String> createPosts = dbl.createPosts(posts);
		assertEquals(1, createPosts.size());
		
		Post<? extends Resource> savedPost = null;
		try {
			savedPost = dbl.getPostDetails(createPosts.get(0), TEST_REQUEST_USER_NAME);
		} catch (Exception ex) {
			// ignore
		}
		
		// get the contentId if more than tags were updated the contentId changes
		final int contentId = savedPost.getContentId();
		
		/*
		 * modify the post; add and remove one tag
		 */
		ModelUtils.addToTagSet(savedPost.getTags(), "newTag");
		savedPost.getTags().remove(new Tag("testCenterTag"));
		
		
		// update url (not tags)
		final Bookmark bookmark = (Bookmark) savedPost.getResource();
		bookmark.setUrl("http://test2.com");
		
		/*
		 * update the post
		 */
		final List<Post<?>> updates = new LinkedList<Post<?>>();
		updates.add(savedPost);
		
		final List<String> updatedPosts = dbl.updatePosts(updates, PostUpdateOperation.UPDATE_TAGS);
		assertEquals(1, updatedPosts.size());
		
		/*
		 * check if only tags were updated
		 */
		Post<? extends Resource> updatedResource = null;
		try {
			updatedResource = dbl.getPostDetails(createPosts.get(0), TEST_REQUEST_USER_NAME);
		} catch (Exception ex) {
			// ignore
		}
		assertNotNull(updatedResource);
		
		// check content id
		assertEquals(contentId, updatedResource.getContentId());
		
		// check tags
		assertTagsByName(ModelUtils.getTagSet("org.bibsonomy.testutil.ModelUtils", "hurz", "secondTag", "newTag"), updatedResource.getTags());
		
		// check if url was not updated
		assertEquals(url, ((Bookmark) updatedResource.getResource()).getUrl());
	}
	
	/**
	 * tests the {@link PostUpdateOperation#UPDATE_ALL}	
	 */
	@Test
	public void updateOperationAll() {
		final LogicInterface dbl = this.getDbLogic(TEST_REQUEST_USER_NAME);
		
		final Post<Bookmark> post = ModelUtils.generatePost(Bookmark.class);
		post.getResource().setUrl("http://www.notest.org");
		post.getResource().recalculateHashes();
		
		final List<Post<?>> create = new LinkedList<Post<?>>();
		create.add(post);
		
		final List<String> createdPosts = dbl.createPosts(create);
		assertEquals(1, createdPosts.size());
		
		Post<?> createdPost = null;
		try {
			createdPost = dbl.getPostDetails(createdPosts.get(0), TEST_REQUEST_USER_NAME);
		} catch (Exception ex) {
			// ignore
		}
		
		final Bookmark createdBookmark = (Bookmark) createdPost.getResource();
		
		final String newURL = "http://www.testAll2.com";
		createdBookmark.setUrl(newURL);
		
		final List<Post<?>> update = new LinkedList<Post<?>>();
		update.add(createdPost);
		
		final List<String> updatedPosts = dbl.updatePosts(update, PostUpdateOperation.UPDATE_ALL);
		assertEquals(1, updatedPosts.size());
		
		Post<?> updatedPost = null;
		try {
			updatedPost = dbl.getPostDetails(updatedPosts.get(0), TEST_REQUEST_USER_NAME);
		} catch (Exception ex) {
			// ignore
		}
		
		final Bookmark updatedBookmark = (Bookmark) updatedPost.getResource();
		assertEquals(newURL, updatedBookmark.getUrl());
	}
}