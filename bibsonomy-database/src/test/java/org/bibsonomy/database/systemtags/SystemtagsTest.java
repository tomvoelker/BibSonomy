package org.bibsonomy.database.systemtags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.common.exceptions.database.DatabaseException;
import org.bibsonomy.database.DBLogicUserInterfaceFactory;
import org.bibsonomy.database.managers.AbstractDBLogicBase;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.systemstags.SystemTag;
import org.bibsonomy.database.systemstags.SystemTagFactory;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.testutil.ParamUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Andreas Koch
 * @version $Id$
 */
public class SystemtagsTest extends AbstractDBLogicBase {

	private SystemTagFactory systemTagFactory;
	
	@Override
	@Before
	@Ignore
	public void setUp() {
		super.setUp();
		systemTagFactory = new SystemTagFactory();
	}

	@Test
	@Ignore
	public void testAttribute() {
		String groupingTag = "sys:grouping";
		final BibTexParam param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.USER, "testuser", Arrays.asList(new String[] { groupingTag }), "", null, 0, 50, null, null, new User("testuser"));
		Assert.assertEquals(GroupingEntity.USER, param.getGrouping());
	}

	@Test
	@Ignore
	public void testFormat() {
		String systemtag = "sys:date:12:03:1983";
		final BibTexParam param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.USER, "testuser", Arrays.asList(new String[] { systemtag }), "", null, 0, 50, null, null, new User("testuser"));
		Assert.assertEquals(GroupingEntity.FRIEND, param.getGrouping());
	}

	@Test
	@Ignore
	public void testFormatFalse() {
		String systemtag = "sys:date:12:03:3";
		final BibTexParam param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.USER, "testuser", Arrays.asList(new String[] { systemtag }), "", null, 0, 50, null, null, new User("testuser"));
		Assert.assertEquals(GroupingEntity.USER, param.getGrouping());
	}

	@Test
	@Ignore
	public void testBibtexKey() {
		String systemtag = "sys:bibtexkey:123456";
		final BibTexParam param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.USER, "testuser", Arrays.asList(new String[] { systemtag }), "", null, 0, 50, null, null, new User("testuser"));
		Assert.assertEquals("123456", param.getBibtexKey());
	}

	@Test
	@Ignore
	public void testDays() {
		String systemtag = "sys:days:13";
		final BibTexParam param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.USER, "testuser", Arrays.asList(new String[] { systemtag }), "", null, 0, 50, null, null, new User("testuser"));
		Assert.assertEquals(13, param.getDays());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSpring() {
		// initialize spring bean factory
		ClassPathXmlApplicationContext springBeanFactory = 
			new ClassPathXmlApplicationContext("systemtags-context.xml");
		// create test bean
		HashMap<String, SystemTag> map = (HashMap<String, SystemTag>) 
			springBeanFactory.getBean("executableSystemTagMap");
		
		Assert.assertNotNull(map.get("for"));
	}
	
	@Test
	public void testArgumentExtraction() {
		final Tag testTag1 = new Tag("for:kde");
		final Tag testTag2 = new Tag("sys:for:kde");
		final Tag testTag3 = new Tag("system:for:kde");
		Set<Tag> tags = new HashSet<Tag>();
		tags.add(testTag1);tags.add(testTag2);tags.add(testTag3);
		
		for (Tag tag : tags) {
			Assert.assertEquals("for", SystemTagsUtil.extractName(tag.getName()));
			String result = "";
			String expected = "kde";
			result = SystemTagsUtil.extractArgument(tag.getName());
			Assert.assertEquals(expected, result);
		}
		
		systemTagFactory.removeSystemTag(tags, "for");
		Assert.assertEquals(0, tags.size());
	}
	
	@Test
	public void testForGroupTag() {
		// create users
		User testUser1 = createTestUser("forgroupuser1");
		User testUser2 = createTestUser("forgroupuser2");
		
		// create groups 
		createTestUser("forgroup1");
		createTestUser("forgroup2");
		Group testGroup1 = createTestGroup("forgroup1");
		Group testGroup2 = createTestGroup("forgroup2");
		
		// add users to groups
		this.groupDb.addUserToGroup("forgroup1", "forgroupuser1", this.dbSession);
		this.groupDb.addUserToGroup("forgroup1", "forgroupuser2", this.dbSession);
		this.groupDb.addUserToGroup("forgroup2", "forgroupuser2", this.dbSession);
		
		// update users
		testUser1.setGroups(this.groupDb.getGroupsForUser(testUser1.getName(), this.dbSession));
		testUser2.setGroups(this.groupDb.getGroupsForUser(testUser2.getName(), this.dbSession));

		// create posts
		Set<Tag> tags1 = new HashSet<Tag>();
		Set<Tag> tags2 = new HashSet<Tag>();
		Tag tag1 = new Tag(); tag1.setName("for:forgroup1"); tags1.add(tag1); tags2.add(tag1);
		Tag tag2 = new Tag(); tag2.setName("for:forgroup2"); tags2.add(tag2);
		
		List<Post<?>> posts1 = new LinkedList<Post<?>>();
		List<Post<?>> posts2 = new LinkedList<Post<?>>();
		List<Post<?>> posts3 = new LinkedList<Post<?>>();
		posts1.add(createTestPost(testUser1, tags1));
		posts2.add(createTestPost(testUser2, tags2));
		posts3.add(createTestPost(testUser1, tags2));
		//change posts3 to avoid douplicates 
		posts3.get(0).getResource().setTitle("some other title");
		// store posts
		DBLogicUserInterfaceFactory logicFactory = new DBLogicUserInterfaceFactory(); 
		logicFactory.setDbSessionFactory(getDbSessionFactory());
		LogicInterface logic1 = logicFactory.getLogicAccess(testUser1.getName(), "password");
		LogicInterface logic2 = logicFactory.getLogicAccess(testUser2.getName(), "password");
		
		// Scenario: 
		//    forgroupuser1 is member of forgroup1
		//    forgroupuser2 is member of forgroup1 and forgroup2
		// 
		//    post1 one contains tags 'for:forgroup1'
		//    post2 one contains tags 'for:forgroup1' and 'for:forgroup2'
		//    post3 one contains tags 'for:forgroup1' and 'for:forgroup2'
		//
		//    post1 is owned by forgroupuser1
		//    post2 is owned by forgroupuser2
		//    post3 is owned by forgroupuser1
		//
		//    logic1 is forgroupuser1's instance
		//    logic2 is forgroupuser2's instance
		
		// forgroupuser1 gives post1 to forgroup1
		logic1.createPosts(posts1);
		List<?> retVal = lookupGroupPost(posts1.get(0), logic1, testGroup1.getName());
		Assert.assertEquals(1, retVal.size());
		retVal = lookupGroupPost(posts1.get(0), logic1, testGroup2.getName());
		Assert.assertEquals(0, retVal.size());
		
		// forgroupuser2 gives post1 and post2 to forgroup1
		logic2.createPosts(posts2);
		retVal = lookupGroupPost(posts2.get(0), logic2, testGroup2.getName());
		Assert.assertEquals(1, retVal.size());
		retVal = lookupGroupPost(posts2.get(0), logic2, testGroup2.getName());
		Assert.assertEquals(1, retVal.size());
		
		// forgroupuser1 gives post3 to forgroup2 -- we expect an error
		try {
			logic1.createPosts(posts3);
			Assert.fail("User was not allowed to write post");
		} catch (DatabaseException ex){
			// ignore
		}
		
		// forgroupuser1 gives post2 to forgroup1 and forgroup2 -- we expect an error
		try {
			logic1.createPosts(posts2);
			Assert.fail("User was not allowed to write post");
		} catch (ValidationException ve){
			// ignore
		}
	}
	
	/**
	 *  test funtionality of the ForFriend SystemTag
	 */
	@Test
	public void testForFriendTag(){
		/*
		 * Send an Inbox Message
		 */
		// create users
		User testUser1 = createTestUser("senderUser");
		User testUser2 = createTestUser("receiverUser");
		testUser2.addFriend(testUser1);
		DBLogicUserInterfaceFactory logicFactory = new DBLogicUserInterfaceFactory();
		logicFactory.setDbSessionFactory(getDbSessionFactory());
		LogicInterface logic = logicFactory.getLogicAccess(testUser2.getName(), "password");
		//UserParam param = new UserParam();
		//param.setU
		//this.userDb.createFriendOfUser(param, dbSession);
		logic.createUserRelationship(testUser2.getName(), testUser1.getName(), UserRelation.OF_FRIEND);
		
		// create post
		Set<Tag> tags = new HashSet<Tag>();
		Tag tag = new Tag();
		tag.setName("send:receiverUser");
		tags.add(tag); 
		
		List<Post<?>> posts = new LinkedList<Post<?>>();
		posts.add(createTestPost(testUser1, tags));
		
		// store posts
		logic = logicFactory.getLogicAccess(testUser1.getName(), "password");
		logic.createPosts(posts);
		Assert.assertEquals(1, this.inboxDb.getNumInboxMessages("receiverUser", dbSession));
	}
	
	//------------------------------------------------------------------------
	// helpers
	//------------------------------------------------------------------------
	private Post <Bookmark> createTestPost(User user, Set<Tag> tags) {
		// generate post
		final Post<Bookmark> post = new Post<Bookmark>();
		final Group group = new Group();

		group.setDescription(null);
		group.setName("public");
		group.setGroupId(GroupID.PUBLIC.getId());
		post.getGroups().add(group);

		post.getTags().addAll(tags);
		
		post.setContentId(null); // will be set in storePost()
		post.setDescription("Some description");
		post.setDate(new Date());
		post.setUser(user);
		final Bookmark resource;

		final Bookmark bookmark = new Bookmark();
		bookmark.setCount(0);
		bookmark.setTitle("test");
		bookmark.setUrl("http://www.testurl.orgg");
		bookmark.recalculateHashes();
		resource = bookmark;
		
		post.setResource(resource);

		return post;
	}
	
	/**
	 * Get test user for given name.
	 * 
	 * @param name
	 * @return
	 */
	private User createTestUser( String name ) {
		// lookup
		User user = this.userDb.getUserDetails(name, this.dbSession);
		if( user.getName()!=null ) {
			final List<Post<Bookmark>> posts = 
				this.bookmarkDb.getPostsForUser(null, name, HashID.INTRA_HASH, GroupID.INVALID.getId(), new ArrayList<Integer>(), null, Integer.MAX_VALUE, 0, null, this.dbSession);
			for( Post<Bookmark> post : posts ) {
				this.bookmarkDb.deletePost(name, post.getResource().getHash(), this.dbSession);
			}
		} else {
			user = new User(name);
			user.setRealname("New Testuser");
			user.setEmail("new-testuser@bibsonomy.org");
			user.setHomepage(ParamUtils.EXAMPLE_URL);
			user.setPassword("password");
			user.setApiKey("00000000000000000000000000000000");
			user.getSettings().setDefaultLanguage("zv");
			user.setSpammer(false);
			user.setRole(Role.DEFAULT);
			user.setToClassify(1);
			user.setAlgorithm(null);
			this.userDb.createUser(user, this.dbSession);
		}
		return user;
	}
	
	/**
	 * Get test group for given name.
	 * 
	 * @param name
	 * @return
	 */
	private Group createTestGroup( String name ) {
		Group group = this.groupDb.getGroupByName(name, this.dbSession);
		if( group!=null ) {
			this.groupDb.deleteGroup(name, this.dbSession);
		};
		group = new Group();
		group.setName(name);
		this.groupDb.storeGroup(group, false, this.dbSession);

		return group;
	}

	/**
	 * Lookup given post for given group.
	 * 
	 * @param <T>
	 * @param post
	 * @param logic
	 * @param groupName
	 * @return
	 */
	private <T extends Resource> List<Post<T>> lookupGroupPost(Post<T> post, LogicInterface logic, String groupName ) {
		final GroupingEntity groupingEntity = GroupingEntity.USER;
		List<String> tags = new LinkedList<String>();
		// FIXME: why does GetPostsForGroup chain element not allow hash-selection?
		List<Post<T>> groupPosts = logic.getPosts(
				(Class<T>)post.getResource().getClass(), groupingEntity, groupName, tags, 
				post.getResource().getIntraHash(), null, null, 0, Integer.MAX_VALUE, "");
		return groupPosts;
	}
	
	/**
	 * Tests the most queries which should be useable with the entrytype system tag.
	 */
	@Test
	public void testEntryType(){
		BibTexParam param = null;
		List<Post<BibTex>> posts = null;
		
		/*
		 * tests the GetBibtexForUser query
		 */
		param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.USER, "testuser1", Collections.singletonList("sys:entrytype:Article"), "", Order.ADDED, 0, 50, null, null, new User("testuser1"));
		posts = this.bibTexDb.getPosts(param, this.dbSession);
		
		Assert.assertEquals(0, posts.size());
		
		param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.USER, "testuser1", Collections.singletonList("sys:entrytype:test entrytype"), "", Order.ADDED, 0, 50, null, null, new User("testuser1"));
		posts = this.bibTexDb.getPosts(param, this.dbSession);
		
		Assert.assertEquals(2, posts.size());
		
		/*
		 * tests the GetBibtexByKey query
		 */
		param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.ALL, "testuser1", Collections.singletonList("sys:entrytype:Book"), "", Order.ADDED, 0, 50, null, null, new User("testuser1"));
		param.setNumSimpleConcepts(0);
		param.setNumTransitiveConcepts(0);
		param.setBibtexKey("test bibtexKey");
		posts = this.bibTexDb.getPosts(param, this.dbSession);
		
		Assert.assertEquals(0, posts.size());		
		
		param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.ALL, "testuser1", Collections.singletonList("sys:entrytype:test entrytype"), "", Order.ADDED, 0, 50, null, null, new User("testuser1"));
		param.setNumSimpleConcepts(0);
		param.setNumTransitiveConcepts(0);
		param.setBibtexKey("test bibtexKey");
		posts = this.bibTexDb.getPosts(param, this.dbSession);
		
		Assert.assertEquals(2, posts.size());
		
		/*
		 * tests the GetBibtexByTagNamesAndUser query
		 */
		List<String> tags = new ArrayList<String>();
		tags = new ArrayList<String>();
		tags.add("sys:entrytype:Book");
		tags.add("testbibtex");
		param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.USER, "testuser1", tags, "", Order.ADDED, 0, 50, null, null, new User("testuser1"));
		param.setNumSimpleConcepts(0);
		param.setNumTransitiveConcepts(0);
		param.setNumSimpleTags(1);
		posts = this.bibTexDb.getPosts(param, this.dbSession);
		
		Assert.assertEquals(0, posts.size());		
		
		tags = new ArrayList<String>();
		tags.add("sys:entrytype:test entrytype");
		tags.add("testbibtex");
		param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.USER, "testuser1", tags , "", Order.ADDED, 0, 50, null, null, new User("testuser1"));
		param.setNumSimpleConcepts(0);
		param.setNumTransitiveConcepts(0);
		param.setNumSimpleTags(1);
		posts = this.bibTexDb.getPosts(param, this.dbSession);
		
		Assert.assertEquals(2, posts.size());
		
		/*
		 * tests the GetBibtexByConceptForUser query
		 */
		param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.USER, "testuser1", Collections.singletonList("sys:entrytype:Book"), "", Order.ADDED, 0, 50, null, null, new User("testuser1"));
		param.setNumSimpleConcepts(1);
		param.setNumTransitiveConcepts(0);
		param.setNumSimpleTags(0);
		param.addSimpleConceptName("testbibtex");
		posts = this.bibTexDb.getPosts(param, this.dbSession);
		
		Assert.assertEquals(0, posts.size());
		
		param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.USER, "testuser1", Collections.singletonList("sys:entrytype:test entrytype"), "", Order.ADDED, 0, 50, null, null, new User("testuser1"));
		param.setNumSimpleConcepts(1);
		param.setNumTransitiveConcepts(0);
		param.setNumSimpleTags(0);
		param.addSimpleConceptName("testbibtex");
		posts = this.bibTexDb.getPosts(param, this.dbSession);
		
		Assert.assertEquals(2, posts.size());
		
		/*
		 * tests the GetBibtexForHomePage query
		 */
		param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.ALL, "testuser1", Collections.singletonList("sys:entrytype:Book"), "", Order.ADDED, 0, 50, null, null, new User("testuser1"));
		posts = this.bibTexDb.getPosts(param, this.dbSession);
		
		Assert.assertEquals(0, posts.size());
		
		param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.ALL, "testuser1", Collections.singletonList("sys:entrytype:test entrytype"), "", Order.ADDED, 0, 50, null, null, new User("testuser1"));
		posts = this.bibTexDb.getPosts(param, this.dbSession);
		
		Assert.assertEquals(2, posts.size());
	}
}