/*
 * Created on 13.07.2007
 */
package org.bibsonomy.rest.remotecall;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Classifier;
import org.bibsonomy.common.enums.ClassifierSettings;
import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.InetAddressStatus;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.common.enums.SpamStatus;
import org.bibsonomy.common.enums.StatisticsConstraint;
import org.bibsonomy.common.enums.TagSimilarity;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.model.Author;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.LogicInterfaceFactory;
import org.bibsonomy.rest.RestServlet;
import org.bibsonomy.rest.client.RestLogicFactory;
import org.bibsonomy.testutil.ModelUtils;
import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.AbstractConnector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.resource.Resource;

/**
 * Tests remote calls via an LogicInterface remote proxy.
 * This test starts the whole rest-server webapplication in its own servlet
 * container. It initializes the RestServlet with a special
 * LogicInterfaceFactory-backend, which actually is an easymock object that
 * is used to check if all calls on the remote proxy go through the REST
 * based protocol without being harmed and trigger the same calls on the
 * server side LogicInterface mock as expected. The returnvalues also get
 * recorded into the mock object on the server and are compared with the
 * actual returnvalues of the remote proxy on the client side.
 * 
 * The class itself implements LogicInterface to ensure all methods are
 * contained in this test and to allow testruns of the methods with
 * different arguments passed by method calls in the test methods. 
 * 
 * TODO: we should go over all "excluded properties" and try to unify them,
 *  so that we don't have to modify several locations when a new property 
 *  is added that is to be excluded
 * 
 * @author Jens Illig
 * @author Christian Kramer
 *
 */
//@Ignore // was inserted due to the strange umlaut behaviour on shaun - fix ASAP!!
public class LogicInterfaceProxyTest implements LogicInterface {
	private static final Log log = LogFactory.getLog(LogicInterfaceProxyTest.class);
	private static final String LOGIN_USER_NAME = LogicInterfaceProxyTest.class.getSimpleName();
	private static final String API_KEY = "A P I äöü K e y";
	//private static final String API_KEY = "yetAnother Strange API KEY";
	private static Server server;
	private static String apiUrl;
	private static LogicInterfaceFactory clientLogicFactory;
	private LogicInterface clientLogic;
	private LogicInterface serverLogic;
	
	/**
	 * configures the server and the webapp and starts the server
	 */
	@BeforeClass
	public static void initServer() {
		try {
			server = new Server();
			AbstractConnector connector = new SocketConnector();
			connector.setHost("127.0.0.1");
			final int port = 41252;
			connector.setPort(port);
			apiUrl = "http://localhost:" + port + "/api";
			server.addConnector(connector);
			Context servletContext = new Context();
			servletContext.setContextPath("/api");
			final Resource resource = Resource.newResource("API_URL");
			resource.setAssociate(apiUrl);
			servletContext.setBaseResource(resource);
			ServletHolder restServlet = servletContext.addServlet(RestServlet.class, "/*");
			restServlet.setInitParameter(RestServlet.PARAM_LOGICFACTORY_CLASS, MockLogicFactory.class.getName() );			
			server.addHandler(servletContext);
			server.start();
			connector.start();
			
			clientLogicFactory = new RestLogicFactory(apiUrl);
		} catch (Exception ex) {
			log.fatal(ex.getMessage(),ex);
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * builds a new mock backend on the serer and a new remote proxy on the client for each test-run
	 */
	@Before
	public void setUp() {
		this.clientLogic = clientLogicFactory.getLogicAccess(LOGIN_USER_NAME, API_KEY);
		this.serverLogic = EasyMock.createMock(LogicInterface.class);
		EasyMock.expect(serverLogic.getAuthenticatedUser()).andReturn(new User(LOGIN_USER_NAME)).anyTimes();
		MockLogicFactory.init(serverLogic);
	}

	/**
	 * resets the mock object on the server
	 */
	@After
	public void tearDown() {
		EasyMock.reset(this.serverLogic);
	}
	
	private void assertLogin() {
		Assert.assertEquals(LOGIN_USER_NAME, MockLogicFactory.getRequestedLoginName());
		Assert.assertEquals(API_KEY, MockLogicFactory.getRequestedApiKey());
		System.out.println(API_KEY + " +++ " + MockLogicFactory.getRequestedApiKey());
	}
	
	/** IArgumentMatcher Implementation that wraps an object and compares it with another */
	private static class PropertyEqualityArgumentMatcher<T> implements IArgumentMatcher {
		private final T a;
		private final String[] excludeProperties; 
		
		private PropertyEqualityArgumentMatcher(final T a, final String... excludeProperties) {
			this.a = a;
			this.excludeProperties = excludeProperties;
		}
		
		public void appendTo(StringBuffer arg0) {
			arg0.append("hurz");
		}

		public boolean matches(Object b) {
			try {
				ModelUtils.assertPropertyEquality(a, b, 5, null, excludeProperties);
			} catch (Throwable t) {
				log.error(t,t);
				return false;
			}
			return true;
		}
		
		/**
		 * tells easymock how the next argument has to be compared and returns the next argument itself
		 * @param <T> Type of the argument
		 * @param a the argument
		 * @param excludeProperties array of propertynames, which shall be left out in comparison 
		 * @return the next argument
		 */
		public static <T> T eq(final T a, final String... excludeProperties) {
			EasyMock.reportMatcher(new PropertyEqualityArgumentMatcher<T>(a, excludeProperties));
			return a;
		}
	}
	
	/**
	 * runs the test defined by {@link #addUserToGroup(String, String)} with certain arguments
	 */
	@Test
	public void addUserToGroupTest() {
		this.addUserToGroup("groupName", "userName");
	}
	public void addUserToGroup(final String groupName, final String userName) {
		// lowercasing the username is necessary here, as this is done
		// by default when calling user.setName(userName)
		serverLogic.addUserToGroup(groupName, userName.toLowerCase());
		EasyMock.replay(serverLogic);
		clientLogic.addUserToGroup(groupName, userName);
		//EasyMock.verify(serverLogic);
		assertLogin();
	}

	/**
	 * runs the test defined by {@link #createGroup(Group)} with certain arguments
	 */
	@Test
	public void createGroupTest() {
		createGroup(ModelUtils.getGroup());
	}
	public String createGroup(Group group) {

		/*
		 * FIXME: remove this line. It is here only, because privlevel is not included 
		 * in the XML and hence not transported to the serverLogic.
		 */
		group.setPrivlevel(null); 
		
		EasyMock.expect(serverLogic.createGroup(PropertyEqualityArgumentMatcher.eq(group, "groupId"))).andReturn(group.getName() + "-new");
		EasyMock.replay(serverLogic);
		Assert.assertEquals(group.getName() + "-new", clientLogic.createGroup(group));
		EasyMock.verify(serverLogic);
		assertLogin();
		return null;
	}
	
	/**
	 * runs the test defined by {@link #createPost(Post)} with a populated Bookmark Post as the argument
	 */
	@Test
	public void createPostTestBookmark() {
		final List<Post<?>> posts = new LinkedList<Post<?>>();
		posts.add(ModelUtils.generatePost(Bookmark.class));
		createPosts(posts);
	}
	/**
	 * runs the test defined by {@link #createPost(Post)} with a populated BibTex Post as the argument
	 */
	@Test
	public void createPostTestBibtex() {
		final List<Post<?>> posts = new LinkedList<Post<?>>();
		posts.add(ModelUtils.generatePost(BibTex.class));
		createPosts(posts);
	}
	public List<String> createPosts(List<Post<?>> posts) {
		final Post<?> post = posts.get(0);
		post.getUser().setName(LOGIN_USER_NAME);
				
		final List<String> singletonList = Collections.singletonList(post.getResource().getIntraHash());

		EasyMock.expect(serverLogic.createPosts(PropertyEqualityArgumentMatcher.eq(posts, "[0].date", "[0].user.apiKey", "[0].user.email", "[0].user.homepage", "[0].user.password", "[0].user.realname", "[0].resource.scraperId", "[0].resource.openURL", "[0].user.IPAddress", "[0].user.basket", "[0].user.inbox", "[0].user.gender", "[0].user.interests", "[0].user.hobbies", "[0].user.profession", "[0].user.openURL", "[0].user.place", "[0].user.spammer", "[0].user.settings", "[0].user.algorithm", "[0].user.prediction", "[0].user.mode", "[0].user.toClassify", "[0].user.updatedBy", "[0].user.reminderPassword", "[0].user.openID"))).andReturn(singletonList);
		EasyMock.replay(serverLogic);
		Assert.assertEquals(singletonList, clientLogic.createPosts(posts));
		EasyMock.verify(serverLogic);
		assertLogin();
		return null;
	}
	
	/**
	 * runs the test defined by {@link #createUser(User)} with a certain argument
	 */
	@Test
	public void createUserTest() {
		createUser(ModelUtils.getUser());
	}
	public String createUser(User user) {

		final User eqUser = PropertyEqualityArgumentMatcher.eq(user, "apiKey", "IPAddress", "basket",  "inbox", "gender", "interests", "hobbies", "profession", "openURL", "place", "spammer", "settings", "toClassify", "updatedBy", "reminderPassword", "openID");
		
		final String userName = serverLogic.createUser(eqUser);
		System.out.println("username is " + userName);
		
		EasyMock.expect(userName).andReturn(user.getName() + "-new");
		EasyMock.replay(serverLogic);
		Assert.assertEquals(user.getName() + "-new", clientLogic.createUser(user));
		EasyMock.verify(serverLogic);
		assertLogin();
		return null;
	}

	/**
	 * runs the test defined by {@link #deleteGroup(String)} with a certain argument
	 */
	@Test
	public void deleteGroupTest() {
		deleteGroup("hurzelGroupName");
	}
	public void deleteGroup(String groupName) {
		serverLogic.deleteGroup(groupName);
		EasyMock.replay(serverLogic);
		clientLogic.deleteGroup(groupName);
		EasyMock.verify(serverLogic);
		assertLogin();
	}

	/**
	 * runs the test defined by {@link #deletePost(String, String)} with certain arguments
	 */
	@Test
	public void deletePostTest() {
		deletePosts("hurzelUserName", Collections.singletonList(ModelUtils.getBookmark().getIntraHash()));
	}
	public void deletePosts(String userName, List<String> resourceHashes) {
		serverLogic.deletePosts(userName, resourceHashes);
		EasyMock.replay(serverLogic);
		clientLogic.deletePosts(userName, resourceHashes);
		EasyMock.verify(serverLogic);
		assertLogin();
	}

	/**
	 * runs the test defined by {@link #deleteUser(String)} with a certain argument
	 */
	@Test
	public void deleteUserTest() {
		deleteUser("hurzelUserName");
	}
	public void deleteUser(String userName) {
		serverLogic.deleteUser(userName);
		EasyMock.replay(serverLogic);
		clientLogic.deleteUser(userName);
		EasyMock.verify(serverLogic);
		assertLogin();
	}

	public User getAuthenticatedUser() {
		// no need to test this as it is part of the client
		return null;
	}

	/**
	 * runs the test defined by {@link #getGroupDetails(String)} with a certain argument
	 */
	@Test
	public void getGroupDetailsTest() {
		getGroupDetails("hurzelGroupName");
	}
	public Group getGroupDetails(String groupName) {
		final Group returnedGroupExpectation = ModelUtils.getGroup();
		
		/*
		 * FIXME: remove this line. It is here only, because privlevel is not included 
		 * in the XML and hence not transported to the serverLogic.
		 */
		returnedGroupExpectation.setPrivlevel(null); 
		
		returnedGroupExpectation.setUsers(new ArrayList<User>());
		returnedGroupExpectation.getUsers().add(ModelUtils.getUser());
		returnedGroupExpectation.getUsers().get(0).setName("Nr1");
		returnedGroupExpectation.getUsers().add(ModelUtils.getUser());
		for (final User u : returnedGroupExpectation.getUsers()) {
			u.setApiKey(null);
			u.setPassword(null);
		}
		EasyMock.expect(serverLogic.getGroupDetails(groupName)).andReturn(returnedGroupExpectation);
		EasyMock.replay(serverLogic);
		final Group returnedGroup = clientLogic.getGroupDetails(groupName);
		System.out.println(returnedGroup.getUser().getIPAddress());
		ModelUtils.assertPropertyEquality(returnedGroupExpectation, returnedGroup, 5, Pattern.compile(".*users.*\\.(apiKey|homepage|realname|email|password|date|openURL|gender|place|interests|hobbies|IPAddress|basket|profession|place|spammer|settings|toClassify|updatedBy)|.*\\.date|.*\\.scraperId|.*\\.openURL|.*groupId|user.*"));
		EasyMock.verify(serverLogic);
		assertLogin();
		return returnedGroup;
	}
	
	/**
	 * runs the test defined by {@link #getGroups(int, int)} with certain arguments
	 */
	@Test
	public void getGroupsTest() {
		getGroups(64, 129);
	}
	public List<Group> getGroups(int start, int end) {
		final List<Group> expectedList = new ArrayList<Group>();
		expectedList.add(ModelUtils.getGroup());
		expectedList.get(0).setName("Group1");
		expectedList.get(0).setGroupId(42);
		/*
		 * FIXME: remove this line. It is here only, because privlevel is not included 
		 * in the XML and hence not transported to the serverLogic.
		 */
		expectedList.get(0).setPrivlevel(null); 
		expectedList.add(ModelUtils.getGroup());
		expectedList.get(1).setName("Group2");
		expectedList.get(0).setGroupId(23);
		/*
		 * FIXME: remove this line. It is here only, because privlevel is not included 
		 * in the XML and hence not transported to the serverLogic.
		 */
		expectedList.get(1).setPrivlevel(null);
		
		EasyMock.expect(serverLogic.getGroups(start, end)).andReturn(expectedList);
		EasyMock.replay(serverLogic);
		final List<Group> returnedGroups = clientLogic.getGroups(start,end);
		ModelUtils.assertPropertyEquality(expectedList, returnedGroups, 3, Pattern.compile(".*\\.groupId"));
		EasyMock.verify(serverLogic);
		assertLogin();
		return returnedGroups;
	}
	
	/**
	 * runs the test defined by {@link #getPostDetails(String, String)} with certain arguments
	 */
	@Test
	public void getPostDetailsTest() {
		getPostDetails(ModelUtils.getBibTex().getIntraHash(), "testUser");
	}
	public Post<? extends org.bibsonomy.model.Resource> getPostDetails(String resourceHash, String userName) {
		final Post<BibTex> expectedBibtexPost = ModelUtils.generatePost(BibTex.class);
		final Post<Bookmark> expectedBookmarkPost = ModelUtils.generatePost(Bookmark.class);
		
		EasyMock.expect(serverLogic.getPostDetails(resourceHash, userName)).andReturn((Post) expectedBibtexPost);
		EasyMock.expect(serverLogic.getPostDetails(resourceHash, userName)).andReturn((Post) expectedBookmarkPost);
		EasyMock.replay(serverLogic);
		
		final String[] ignoreProperties = new String[] {"date", "user.apiKey", "user.email", "user.homepage", "user.password", "user.realname", "resource.scraperId", "resource.openURL", "user.IPAddress", "user.basket", "user.inbox", "user.gender", "user.interests", "user.hobbies", "user.profession", "user.openURL", "user.place", "user.spammer", "user.settings", "user.algorithm", "user.prediction", "user.mode", "user.toClassify", "user.updatedBy", "user.reminderPassword", "user.openID"};
		final Post<? extends org.bibsonomy.model.Resource> returnedBibtexPost = clientLogic.getPostDetails(resourceHash,userName);
		ModelUtils.assertPropertyEquality(expectedBibtexPost, returnedBibtexPost, 5, null, ignoreProperties);
		final Post<? extends org.bibsonomy.model.Resource> returnedBookmarkPost = clientLogic.getPostDetails(resourceHash,userName);
		ModelUtils.assertPropertyEquality(expectedBookmarkPost, returnedBookmarkPost, 5, null, ignoreProperties);
		EasyMock.verify(serverLogic);
		assertLogin();
		return returnedBibtexPost;
	}
	
	/**
	 * runs the test defined by {@link #getPosts(Class, GroupingEntity, String, List, String, Order, int, int, String)} with arguments as used for the getBookmarkByTagName query
	 */
	@Test
	public void getPostsTestBookmarkByTag() {
		getPosts(Bookmark.class, GroupingEntity.ALL, null, Arrays.asList("bla", "blub"), null, null /* must be null because order is inferred and not transmitted */, null,  7, 1264, null);
	}
	/**
	 * runs the test defined by {@link #getPosts(Class, GroupingEntity, String, List, String, Order, int, int, String)} with arguments as used for the getBibtexForGroupAndTag query
	 */
	@Test
	public void getPostsTestBibtexByGroupAndTag() {
		getPosts(BibTex.class, GroupingEntity.GROUP, "testGroup", Arrays.asList("blub", "bla"), null, null, null, 0, 1, null);
	}
	/**
	 * runs the test defined by {@link #getPosts(Class, GroupingEntity, String, List, String, Order, int, int, String)} with arguments as used for the getBibtexByHashForUser query 
	 */
	@Test
	public void getPostsTestBibtexByUserAndHash() {
		getPosts(BibTex.class, GroupingEntity.USER, "testUser", new ArrayList<String>(0), ModelUtils.getBibTex().getIntraHash(), null, null, 0, 5, null);
	}
	public <T extends org.bibsonomy.model.Resource> List<Post<T>> getPosts(Class<T> resourceType, GroupingEntity grouping, String groupingName, List<String> tags, String hash, Order order, FilterEntity filter, int start, int end, String search) {
		final List<Post<T>> expectedPosts = new ArrayList<Post<T>>();
		expectedPosts.add(ModelUtils.generatePost(resourceType));
		expectedPosts.get(0).setDescription("erstes");
		expectedPosts.add(ModelUtils.generatePost(resourceType));
		if (resourceType == org.bibsonomy.model.Resource.class) {
			expectedPosts.add( (Post) ModelUtils.generatePost(Bookmark.class));
			expectedPosts.add( (Post) ModelUtils.generatePost(BibTex.class));
		}
		
		EasyMock.expect(serverLogic.getPosts(resourceType, grouping, groupingName, tags, hash, order, filter, start, end, search)).andReturn(expectedPosts);
		EasyMock.replay(serverLogic);

		final List<Post<T>> returnedPosts = clientLogic.getPosts(resourceType, grouping, groupingName, tags, hash, order, filter, start, end, search);
		ModelUtils.assertPropertyEquality(expectedPosts, returnedPosts, 5, Pattern.compile(".*\\.user\\.(apiKey|homepage|realname|email|password|date|openURL|gender|place|interests|hobbies|IPAddress|basket|inbox|profession|place|spammer|settings|prediction|algorithm|mode|toClassify|updatedBy|reminderPassword|openID)|.*\\.date|.*\\.scraperId|.*\\.openURL"));
		// "user.apiKey", "user.email", "user.homepage", "user.password", "user.realname", "resource.scraperId", "resource.openURL", "user.IPAddress", "user.basket", "user.inbox", "user.gender", "user.interests", "user.hobbies", "user.profession", "user.openURL", "user.place", "user.spammer", "user.settings"
		EasyMock.verify(serverLogic);
		assertLogin();
		return returnedPosts;
	}


	/**
	 * runs the test defined by {@link #getTagDetails(String)} with a certain argument
	 */
	@Test
	public void getTagDetailsTest() {
		getTagDetails("testzeug");
	}	
	public Tag getTagDetails(String tagName) {
		final Tag expected = ModelUtils.getTag();		
		EasyMock.expect(serverLogic.getTagDetails(tagName)).andReturn(expected);
		EasyMock.replay(serverLogic);
		
		final Tag returned = clientLogic.getTagDetails(tagName);
		ModelUtils.assertPropertyEquality(expected, returned, 3, Pattern.compile("(.*\\.)?(id|stem)"));
		EasyMock.verify(serverLogic);
		assertLogin();
		return returned;
	}

	
	/**
	 * runs the test defined by {@link #getTags(Class, GroupingEntity, String, String, int, int)} with certain arguments
	 */
	@Test
	public void getTagsTest() {
		getTags(org.bibsonomy.model.Resource.class, GroupingEntity.GROUP, "testGroup", "regex", null, null, null, 4, 22, null, null);
	}
	public List<Tag> getTags(Class<? extends org.bibsonomy.model.Resource> resourceType, GroupingEntity grouping, String groupingName, String regex, List<String> tags, String hash, Order order, int start, int end, String search, TagSimilarity relation) {
		final List<Tag> expected = ModelUtils.buildTagList(3, "testPrefix", 1);		
		EasyMock.expect(serverLogic.getTags(resourceType, grouping, groupingName, regex, tags, null, order, start, end, null, null)).andReturn(expected);
		EasyMock.replay(serverLogic);
		
		final List<Tag> returned = clientLogic.getTags(resourceType, grouping, groupingName, regex, tags, null, order, start, end, null, null);
		ModelUtils.assertPropertyEquality(expected, returned, 5, Pattern.compile("(.*\\.)?(id|stem)"));
		EasyMock.verify(serverLogic);
		assertLogin();
		return returned;
	}

	/**
	 * runs the test defined by {@link #getUserDetails(String)} with a certain arguments
	 */
	@Test
	public void getUserDetailsTest() {
		getUserDetails("usrName");
	}
	public User getUserDetails(String userName) {
		final User expected = ModelUtils.getUser();		
		EasyMock.expect(serverLogic.getUserDetails(userName)).andReturn(expected);
		EasyMock.replay(serverLogic);
		
		final User returned = clientLogic.getUserDetails(userName);
		ModelUtils.assertPropertyEquality(expected, returned, 3, null, "apiKey", "email", "homepage", "password", "realname", "date", "openURL", "gender", "place", "IPAddress", "basket", "inbox", "profession", "place", "spammer", "settings", "hobbies", "interests", "toClassify", "updatedBy", "reminderPassword", "openID");
		// (apiKey|homepage|realname|email|password|date|openURL|gender|place|interests|hobbies|IPAddress|basket|inbox|profession|place|spammer|settings)
		EasyMock.verify(serverLogic);
		assertLogin();
		return returned;
	}
	

	/**
	 * runs the test defined by {@link #removeUserFromGroup(String, String)} with certain arguments
	 */
	@Test
	public void deleteUserFromGroupTest() {
		deleteUserFromGroup("grooouuup!", "userTest");
	}
	public void deleteUserFromGroup(String groupName, String userName) {
		serverLogic.deleteUserFromGroup(groupName, userName);
		EasyMock.replay(serverLogic);
		clientLogic.deleteUserFromGroup(groupName, userName);
		EasyMock.verify(serverLogic);
		assertLogin();
	}

	/**
	 * runs the test defined by {@link #updateGroup(Group)} with certain group argument
	 */	
	@Test
	public void updateGroupTest() {
		updateGroup(ModelUtils.getGroup());
	}
	public String updateGroup(Group group) {
		
		/*
		 * FIXME: remove this line. It is here only, because privlevel is not included 
		 * in the XML and hence not transported to the serverLogic.
		 */
		group.setPrivlevel(null); 
		
		EasyMock.expect(serverLogic.updateGroup(PropertyEqualityArgumentMatcher.eq(group, "groupId"))).andReturn(group.getName() + "-new");
		EasyMock.replay(serverLogic);
		Assert.assertEquals(group.getName() + "-new", clientLogic.updateGroup(group));
		EasyMock.verify(serverLogic);
		assertLogin();
		return null;
	}

	/**
	 * runs the test defined by {@link #updatePosts(List, PostUpdateOperation)} with a fully populated Bibtex Post as argument
	 */
	@Test
	public void updatePostTestBibtex() {
		final List<Post<?>> posts = new LinkedList<Post<?>>();
		posts.add(ModelUtils.generatePost(BibTex.class));

		updatePosts(posts, PostUpdateOperation.UPDATE_ALL);
	}
	/**
	 * runs the test defined by {@link #updatePosts(List, PostUpdateOperation)} with a fully populated Bookmark Post as argument
	 */
	@Test
	public void updatePostTestBookmark() {
		final List<Post<?>> posts = new LinkedList<Post<?>>();
		posts.add(ModelUtils.generatePost(Bookmark.class));
		
		updatePosts(posts, PostUpdateOperation.UPDATE_ALL);
	}
	
	public List<String> updatePosts(List<Post<?>> posts, PostUpdateOperation operation) {
		final Post<?> post = posts.get(0);
		post.getUser().setName(LOGIN_USER_NAME);
		
		final List<String> singletonList = Collections.singletonList(post.getResource().getIntraHash());
		
		EasyMock.expect(serverLogic.updatePosts(PropertyEqualityArgumentMatcher.eq(posts, "[0].date", "[0].user.apiKey", "[0].user.email", "[0].user.homepage", "[0].user.password", "[0].user.realname", "[0].resource.scraperId", "[0].resource.openURL", "[0].user.IPAddress", "[0].user.basket", "[0].user.inbox", "[0].user.gender", "[0].user.interests", "[0].user.hobbies", "[0].user.profession", "[0].user.openURL", "[0].user.place", "[0].user.spammer", "[0].user.settings", "[0].user.algorithm", "[0].user.prediction", "[0].user.mode", "[0].user.updatedBy", "[0].user.toClassify", "[0].user.reminderPassword", "[0].user.openID"), PropertyEqualityArgumentMatcher.eq(operation, ""))).andReturn(singletonList);
		EasyMock.replay(serverLogic);
		Assert.assertEquals(singletonList, clientLogic.updatePosts(posts, operation));
		EasyMock.verify(serverLogic);
		assertLogin();
		return null;
		
	}

	/**
	 * runs the test defined by {@link #updateUser(User)} with a certain argument
	 */
	@Test
	public void updateUserTest() {
		createUser(ModelUtils.getUser());
	}
	public String updateUser(User user) {
		EasyMock.expect(serverLogic.createUser(PropertyEqualityArgumentMatcher.eq(user, "apiKey"))).andReturn("rVal");
		EasyMock.replay(serverLogic);
		Assert.assertEquals("rVal", clientLogic.createUser(user));
		EasyMock.verify(serverLogic);
		assertLogin();
		return null;
	}
	
	/**
	 * stops the servlet container after all tests have been run
	 */
	@AfterClass
	public static void shutdown() {
		try {
			server.stop();
		} catch (Exception ex) {
			log.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex);
		}
	}

	public String createDocument(Document doc, String resourceHash) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Document getDocument(String userName, String fileHash) {
		
		return null;
	}

	public Document getDocument(String userName, String resourceHash, String fileName) {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteDocument(Document document, String fileName) {
		// TODO Auto-generated method stub
		
	}

	public void createInetAddressStatus(InetAddress address, InetAddressStatus status) {
		// TODO Auto-generated method stub
		
	}

	public void deleteInetAdressStatus(InetAddress address) {
		// TODO Auto-generated method stub
		
	}

	public InetAddressStatus getInetAddressStatus(InetAddress address) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<Tag> getConcepts(Class<? extends org.bibsonomy.model.Resource> resourceType, GroupingEntity grouping, String groupingName, String regex, List<String> tags, ConceptStatus status, int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}

	public String createConcept(Tag concept, GroupingEntity grouping, String groupingName) {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteConcept(String concept, GroupingEntity grouping, String groupingName) {
		// TODO Auto-generated method stub
		
	}

	public void deleteRelation(String upper, String lower, GroupingEntity grouping, String groupingName) {
		// TODO Auto-generated method stub
		
	}

	public Tag getConceptDetails(String conceptName, GroupingEntity grouping, String groupingName) {
		// TODO Auto-generated method stub
		return null;
	}

	public String updateConcept(Tag concept, GroupingEntity grouping, String groupingName) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * runs the test defined by {@link #getUsers(Class, GroupingEntity, String, List, String, Order, UserRelation, String, int, int)} with certain arguments
	 * (in order to retrieve all users)
	 */
	@Test
	public void getAllUsersTest() {
		//getUsers(1,56);
		getUsers(null, GroupingEntity.ALL, null, null, null, null, null, null, 1, 56);
	}

	/**
	 * runs the test defined by {@link #getUsers(Class, GroupingEntity, String, List, String, Order, UserRelation, String, int, int)} with certain arguments
	 * (in order to retrieve group members)
	 */
	@Test
	public void getGroupMembersTest() {
		getUsers(null, GroupingEntity.GROUP, "grpX", null, null, null, null, null, 1, 56);				
	}	
	
	public List<User> getUsers(Class<? extends org.bibsonomy.model.Resource> resourceType, GroupingEntity grouping, String groupingName, List<String> tags, String hash, Order order, UserRelation relation, String search, int start, int end) {
		List<User> expected = new ArrayList<User>(2);
		expected.add(ModelUtils.getUser());
		expected.get(0).setName("Nr1");
		expected.add(ModelUtils.getUser());
		expected.get(1).setName("Nr2");
		EasyMock.expect(serverLogic.getUsers(resourceType, grouping, groupingName, tags, hash, order, relation, search, start, end)).andReturn(expected);
		EasyMock.replay(serverLogic);		
		final List<User> returned = clientLogic.getUsers(resourceType, grouping, groupingName, tags, hash, order, relation, search, start, end);
		ModelUtils.assertPropertyEquality(expected, returned, 5, Pattern.compile(".*\\.(apiKey|homepage|realname|email|password|date|openURL|gender|place|interests|hobbies|IPAddress|basket|inbox|profession|place|spammer|settings|toClassify|updatedBy|reminderPassword|openID)"));
		EasyMock.verify(serverLogic);
		assertLogin();			
		return returned;
	}
	
	public int getStatistics(Class<? extends org.bibsonomy.model.Resource> arg0, GroupingEntity arg1, String arg2, StatisticsConstraint arg3, String arg4, List<String> arg5) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getClassifierSettings(ClassifierSettings key) {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateClassifierSettings(ClassifierSettings key, String value) {
		// TODO Auto-generated method stub		
	}

	public int getClassifiedUserCount(Classifier classifier, SpamStatus status, int interval) {
		// TODO Auto-generated method stub
		return 0;
	}

	public List<User> getClassifiedUsers(Classifier classifier, SpamStatus status, int interval) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<User> getClassifierHistory(String userName) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<User> getClassifierComparison(int interval) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getPostStatistics(Class<? extends org.bibsonomy.model.Resource> resourceType, GroupingEntity grouping, String groupingName, List<String> tags, String hash, Order order, FilterEntity filter, int start, int end, String search, StatisticsConstraint constraint) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getOpenIDUser(String openID) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** Does nothing, because it's not implemented in the API.
	 * @param user
	 * @param tagsToReplace
	 * @param replacementTags
	 * @return
	 */
	public int updateTags(final User user, final List<Tag> tagsToReplace, final List<Tag> replacementTags) {
		// TODO Auto-generated method stub
		return 0;
	}


	public int getTagStatistics(Class<? extends org.bibsonomy.model.Resource> resourceType, GroupingEntity grouping, String groupingName, String regex, List<String> tags, ConceptStatus status, int start, int end) {
		// TODO Auto-generated method stub
		return 0;
	}

	public List<User> getFriendsOfUser(User loginUser) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<User> getUserFriends(User loginUser) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Author> getAuthors(GroupingEntity grouping, String groupingName, List<String> tags, String hash, Order order, FilterEntity filter, int start, int end, String search) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteUserRelationship(User loggedInUser, User requestedUser, UserRelation relation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createUserRelationship(User loggedInUser, User requestedUser, UserRelation relation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int createBasketItems(List<Post<? extends org.bibsonomy.model.Resource>> posts) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int deleteBasketItems(List<Post<? extends org.bibsonomy.model.Resource>> posts, boolean clearBasket) {
		// TODO Auto-generated method stub
		return 0;
	}
}