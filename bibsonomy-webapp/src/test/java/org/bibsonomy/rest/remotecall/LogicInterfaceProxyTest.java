/*
 * Created on 13.07.2007
 */
package org.bibsonomy.rest.remotecall;

import java.util.List;

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
				ModelUtils.assertPropertyEquality(a, b, 5, excludeProperties);
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
		serverLogic.createPost(PropertyEqualityArgumentMatcher.eq(post,"date", "user.apiKey", "user.email", "user.homepage", "user.password", "user.realname"));
		EasyMock.replay(serverLogic);
		clientLogic.createPost(post);
		EasyMock.verify(serverLogic);
		assertLogin();
	}

	public void createUser(User user) {
		// TODO Auto-generated method stub
		
	}

	public void deleteGroup(String groupName) {
		// TODO Auto-generated method stub
		
	}

	public void deletePost(String userName, String resourceHash) {
		// TODO Auto-generated method stub
		
	}

	public void deleteUser(String userName) {
		// TODO Auto-generated method stub
		
	}

	public String getAuthenticatedUser() {
		// no need to test this as it is part of the client
		return null;
	}

	public Group getGroupDetails(String groupName) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Group> getGroups(int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}

	public Post<? extends org.bibsonomy.model.Resource> getPostDetails(String resourceHash, String userName) {
		// TODO Auto-generated method stub
		return null;
	}

	public <T extends org.bibsonomy.model.Resource> List<Post<T>> getPosts(Class<T> resourceType, GroupingEntity grouping, String groupingName, List<String> tags, String hash, Order order, int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}

	public Tag getTagDetails(String tagName) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Tag> getTags(GroupingEntity grouping, String groupingName, String regex, int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}

	public User getUserDetails(String userName) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<User> getUsers(int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<User> getUsers(String groupName, int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeUserFromGroup(String groupName, String userName) {
		// TODO Auto-generated method stub
		
	}

	public void updateGroup(Group group) {
		// TODO Auto-generated method stub
		
	}

	public void updatePost(Post<?> post) {
		// TODO Auto-generated method stub
		
	}

	public void updateUser(User user) {
		// TODO Auto-generated method stub
		
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
