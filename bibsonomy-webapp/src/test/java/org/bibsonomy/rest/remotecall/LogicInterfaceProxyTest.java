/*
 * Created on 13.07.2007
 */
package org.bibsonomy.rest.remotecall;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.LogicInterfaceFactory;
import org.bibsonomy.model.logic.Order;
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
 * Remote Call Tests
 * 
 * @author Jens Illig
 *
 */
public class LogicInterfaceProxyTest implements LogicInterface {
	private static final Logger log = Logger.getLogger(LogicInterfaceProxyTest.class);
	private static final String LOGIN_USER_NAME = LogicInterfaceProxyTest.class.getSimpleName();
	private static final String API_KEY = "A P I äöü K e y";
	private static Server server;
	private static String apiUrl;
	private static LogicInterfaceFactory clientLogicFactory;
	private LogicInterface clientLogic;
	private LogicInterface serverLogic;
	
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
	
	@Before
	public void setUp() {
		this.clientLogic = clientLogicFactory.getLogicAccess(LOGIN_USER_NAME, API_KEY);
		this.serverLogic = EasyMock.createMock(LogicInterface.class);
		EasyMock.expect(serverLogic.getAuthenticatedUser()).andReturn(LOGIN_USER_NAME).anyTimes();
		MockLogicFactory.init(serverLogic);
	}
	
	@After
	public void tearDown() {
		EasyMock.reset(this.serverLogic);
	}
	
	private void assertLogin() {
		Assert.assertEquals(LOGIN_USER_NAME, MockLogicFactory.getRequestedLoginName());
		Assert.assertEquals(API_KEY, MockLogicFactory.getRequestedApiKey());
	}
	
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
		
		public static <T> T eq(final T a, final String... excludeProperties) {
			EasyMock.reportMatcher(new PropertyEqualityArgumentMatcher<T>(a, excludeProperties));
			return a;
		}
	}
	
	@Test
	public void addUserToGroupTest() {
		this.addUserToGroup("groupName", "userName");
	}
	public void addUserToGroup(final String groupName, final String userName) {
		serverLogic.addUserToGroup(groupName, userName);
		EasyMock.replay(serverLogic);
		clientLogic.addUserToGroup(groupName, userName);
		EasyMock.verify(serverLogic);
		assertLogin();
	}

	@Test
	public void createGroupTest() {
		createGroup(ModelUtils.getGroup());
	}
	public void createGroup(Group group) {
		serverLogic.createGroup(PropertyEqualityArgumentMatcher.eq(group, "groupId"));
		EasyMock.replay(serverLogic);
		clientLogic.createGroup(group);
		EasyMock.verify(serverLogic);
		assertLogin();
	}
	
	@Test
	public void createPostTestBookmark() {
		createPost(ModelUtils.generatePost(Bookmark.class));
	}
	@Test
	public void createPostTestBibtex() {
		createPost(ModelUtils.generatePost(BibTex.class));
	}
	public void createPost(Post<?> post) {
		serverLogic.createPost(PropertyEqualityArgumentMatcher.eq(post,"date", "user.apiKey", "user.email", "user.homepage", "user.password", "user.realname", "resource.scraperId"));
		EasyMock.replay(serverLogic);
		clientLogic.createPost(post);
		EasyMock.verify(serverLogic);
		assertLogin();
	}
	
	@Test
	public void createUserTest() {
		createUser(ModelUtils.getUser());
	}
	public void createUser(User user) {
		serverLogic.createUser(PropertyEqualityArgumentMatcher.eq(user, "apiKey"));
		EasyMock.replay(serverLogic);
		clientLogic.createUser(user);
		EasyMock.verify(serverLogic);
		assertLogin();
	}

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

	
	@Test
	public void deletePostTest() {
		deletePost("hurzelUserName", ModelUtils.getBookmark().getIntraHash());
	}
	public void deletePost(String userName, String resourceHash) {
		serverLogic.deletePost(userName, resourceHash);
		EasyMock.replay(serverLogic);
		clientLogic.deletePost(userName, resourceHash);
		EasyMock.verify(serverLogic);
		assertLogin();
	}

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

	public String getAuthenticatedUser() {
		// no need to test this as it is part of the client
		return null;
	}

	@Test
	public void getGroupDetailsTest() {
		getGroupDetails("hurzelGroupName");
	}
	public Group getGroupDetails(String groupName) {
		final Group returnedGroupExpectation = ModelUtils.getGroup();
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
		ModelUtils.assertPropertyEquality(returnedGroupExpectation, returnedGroup, 5, null, "groupId");
		EasyMock.verify(serverLogic);
		assertLogin();
		return returnedGroup;
	}
	
	
	@Test
	public void getGroupsTest() {
		getGroups(64, 129);
	}
	public List<Group> getGroups(int start, int end) {
		final List<Group> expectedList = new ArrayList<Group>();
		expectedList.add(ModelUtils.getGroup());
		expectedList.get(0).setName("Group1");
		expectedList.add(ModelUtils.getGroup());
		expectedList.get(1).setName("Group2");
		
		EasyMock.expect(serverLogic.getGroups(start, end)).andReturn(expectedList);
		EasyMock.replay(serverLogic);
		final List<Group> returnedGroups = clientLogic.getGroups(start,end);
		ModelUtils.assertPropertyEquality(expectedList, returnedGroups, 3, Pattern.compile(".*\\.groupId"));
		EasyMock.verify(serverLogic);
		assertLogin();
		return returnedGroups;
	}
	
	
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
		
		final String[] ignoreProperties = new String[] {"user.apiKey", "user.email", "user.homepage", "user.password", "user.realname", "date", "resource.scraperId"};
		final Post<? extends org.bibsonomy.model.Resource> returnedBibtexPost = clientLogic.getPostDetails(resourceHash,userName);
		ModelUtils.assertPropertyEquality(expectedBibtexPost, returnedBibtexPost, 5, null, ignoreProperties);
		final Post<? extends org.bibsonomy.model.Resource> returnedBookmarkPost = clientLogic.getPostDetails(resourceHash,userName);
		ModelUtils.assertPropertyEquality(expectedBookmarkPost, returnedBookmarkPost, 5, null, ignoreProperties);
		EasyMock.verify(serverLogic);
		assertLogin();
		return returnedBibtexPost;
	}
	
	
	@Test
	public void getPostsTestBookmarkByTag() {
		getPosts(Bookmark.class, GroupingEntity.ALL, null, Arrays.asList("bla", "blub"), null, null /* must be null because order is inferred and not transmitted */, 7, 1264);
	}
	@Test
	public void getPostsTestBibtexByGroupAndTag() {
		getPosts(BibTex.class, GroupingEntity.GROUP, "testGroup", Arrays.asList("blub", "bla"), null, null, 0, 1);
	}
	@Test
	public void getPostsTestBibtexByUserAndHash() {
		getPosts(BibTex.class, GroupingEntity.USER, "testUser", new ArrayList<String>(0), ModelUtils.getBibTex().getIntraHash(), null, 0, 5);
	}
	public <T extends org.bibsonomy.model.Resource> List<Post<T>> getPosts(Class<T> resourceType, GroupingEntity grouping, String groupingName, List<String> tags, String hash, Order order, int start, int end) {
		final List<Post<T>> expectedPosts = new ArrayList<Post<T>>();
		expectedPosts.add(ModelUtils.generatePost(resourceType));
		expectedPosts.get(0).setDescription("erstes");
		expectedPosts.add(ModelUtils.generatePost(resourceType));
		if (resourceType == org.bibsonomy.model.Resource.class) {
			expectedPosts.add( (Post) ModelUtils.generatePost(Bookmark.class));
			expectedPosts.add( (Post) ModelUtils.generatePost(BibTex.class));
		}
		
		EasyMock.expect(serverLogic.getPosts(resourceType, grouping, groupingName, tags, hash, order, start, end )).andReturn(expectedPosts);
		EasyMock.replay(serverLogic);

		final List<Post<T>> returnedPosts = clientLogic.getPosts(resourceType, grouping, groupingName, tags, hash, order, start, end );
		ModelUtils.assertPropertyEquality(expectedPosts, returnedPosts, 5, Pattern.compile(".*\\.user\\.(apiKey|homepage|realname|email|password|date)|.*\\.date|.*\\.scraperId"));
		EasyMock.verify(serverLogic);
		assertLogin();
		return returnedPosts;
	}


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

	
	@Test
	public void getTagsTest() {
		getTags(GroupingEntity.GROUP, "testGroup", "regex", 4, 22);
	}
	public List<Tag> getTags(GroupingEntity grouping, String groupingName, String regex, int start, int end) {
		final List<Tag> expected = ModelUtils.buildTagList(3, "testPrefix", 1);		
		EasyMock.expect(serverLogic.getTags(grouping, groupingName, regex, start, end)).andReturn(expected);
		EasyMock.replay(serverLogic);
		
		final List<Tag> returned = clientLogic.getTags(grouping, groupingName, regex, start, end);
		ModelUtils.assertPropertyEquality(expected, returned, 5, Pattern.compile("(.*\\.)?(id|stem)"));
		EasyMock.verify(serverLogic);
		assertLogin();
		return returned;
	}

	
	@Test
	public void getUserDetailsTest() {
		getUserDetails("usrName");
	}
	public User getUserDetails(String userName) {
		final User expected = ModelUtils.getUser();		
		EasyMock.expect(serverLogic.getUserDetails(userName)).andReturn(expected);
		EasyMock.replay(serverLogic);
		
		final User returned = clientLogic.getUserDetails(userName);
		ModelUtils.assertPropertyEquality(expected, returned, 3, null, "apiKey", "email", "homepage", "password", "realname");
		EasyMock.verify(serverLogic);
		assertLogin();
		return returned;
	}
	

	@Test
	public void getUsersTest() {
		getUsers(1,56);
	}
	public List<User> getUsers(int start, int end) {
		final List<User> expected = new ArrayList<User>(2);
		expected.add(ModelUtils.getUser());
		expected.get(0).setName("Nr1");
		expected.add(ModelUtils.getUser());
		expected.get(1).setName("Nr2");
		EasyMock.expect(serverLogic.getUsers(start, end)).andReturn(expected);
		EasyMock.replay(serverLogic);
		
		final List<User> returned = clientLogic.getUsers(start, end);
		ModelUtils.assertPropertyEquality(expected, returned, 5, Pattern.compile(".*\\.(apiKey|homepage|realname|email|password)"));
		EasyMock.verify(serverLogic);
		assertLogin();
		return returned;
	}

	@Test
	public void getUsersTestWithGroup() {
		getUsers("grpX",1,56);
	}
	public List<User> getUsers(String groupName, int start, int end) {
		final List<User> expected = new ArrayList<User>(2);
		expected.add(ModelUtils.getUser());
		expected.get(0).setName("nr1");
		expected.add(ModelUtils.getUser());
		expected.get(1).setName("nr2");
		EasyMock.expect(serverLogic.getUsers(groupName, start, end)).andReturn(expected);
		EasyMock.replay(serverLogic);
		
		final List<User> returned = clientLogic.getUsers(groupName, start, end);
		ModelUtils.assertPropertyEquality(expected, returned, 5, Pattern.compile(".*\\.(apiKey|homepage|realname|email|password)"));
		EasyMock.verify(serverLogic);
		assertLogin();
		return returned;
	}

	
	@Test
	public void removeUserFromGroupTest() {
		removeUserFromGroup("grooouuup!", "userTest");
	}
	public void removeUserFromGroup(String groupName, String userName) {
		serverLogic.removeUserFromGroup(groupName, userName);
		EasyMock.replay(serverLogic);
		clientLogic.removeUserFromGroup(groupName, userName);
		EasyMock.verify(serverLogic);
		assertLogin();
	}

	
	@Test
	public void updateGroupTest() {
		updateGroup(ModelUtils.getGroup());
	}
	public void updateGroup(Group group) {
		serverLogic.updateGroup(PropertyEqualityArgumentMatcher.eq(group, "groupId"));
		EasyMock.replay(serverLogic);
		clientLogic.updateGroup(group);
		EasyMock.verify(serverLogic);
		assertLogin();
	}

	
	@Test
	public void updatePostTestBibtex() {
		updatePost(ModelUtils.generatePost(BibTex.class));
	}
	@Test
	public void updatePostTestBookmark() {
		updatePost(ModelUtils.generatePost(Bookmark.class));
	}
	public void updatePost(Post<?> post) {
		serverLogic.updatePost(PropertyEqualityArgumentMatcher.eq(post,"date", "user.apiKey", "user.email", "user.homepage", "user.password", "user.realname", "resource.scraperId"));
		EasyMock.replay(serverLogic);
		clientLogic.updatePost(post);
		EasyMock.verify(serverLogic);
		assertLogin();
	}

	
	@Test
	public void updateUserTest() {
		createUser(ModelUtils.getUser());
	}
	public void updateUser(User user) {
		serverLogic.createUser(PropertyEqualityArgumentMatcher.eq(user, "apiKey"));
		EasyMock.replay(serverLogic);
		clientLogic.createUser(user);
		EasyMock.verify(serverLogic);
		assertLogin();
	}
	
	@AfterClass
	public static void shutdown() {
		try {
			server.stop();
		} catch (Exception ex) {
			log.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex);
		}
	}
}
