/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.rest.remotecall;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Filter;
import org.bibsonomy.common.enums.GroupRole;
import org.bibsonomy.common.enums.GroupUpdateOperation;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.common.enums.SearchType;
import org.bibsonomy.common.enums.TagSimilarity;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.GroupMembership;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.LogicInterfaceFactory;
import org.bibsonomy.model.logic.util.AbstractLogicInterface;
import org.bibsonomy.rest.AuthenticationHandler;
import org.bibsonomy.rest.BasicAuthenticationHandler;
import org.bibsonomy.rest.RestServlet;
import org.bibsonomy.rest.client.RestLogicFactory;
import org.bibsonomy.rest.renderer.RendererFactory;
import org.bibsonomy.rest.renderer.RenderingFormat;
import org.bibsonomy.rest.renderer.UrlRenderer;
import org.bibsonomy.services.filesystem.extension.ListExtensionChecker;
import org.bibsonomy.testutil.CommonModelUtils;
import org.bibsonomy.testutil.ModelUtils;
import org.bibsonomy.util.HashUtils;
import org.bibsonomy.util.file.ServerFileLogic;
import org.bibsonomy.webapp.util.file.document.ServerDocumentFileLogic;
import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.eclipse.jetty.server.AbstractConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.MultipartFilter;

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
 */
public class LogicInterfaceProxyTest extends AbstractLogicInterface {
	
	/*
	 * FIXME: clean up this mess :-(
	 */
	private static final String COMMON_USER_PROPERTIES = "apiKey|homepage|realname|email|password|date|openURL|gender|place|interests|hobbies|IPAddress|basket|inbox|profession|institution|place|spammer|settings|toClassify|updatedBy|gravatarAddress";
	private static final String[] IGNORE1 = new String[] {"[0].date", "[0].user.apiKey", "[0].user.email", "[0].user.homepage", "[0].user.password", "[0].user.passwordSalt", "[0].user.realname", "[0].user.confidence", "[0].resource.scraperId", "[0].resource.openURL", "[0].resource.numberOfRatings", "[0].resource.rating", "[0].user.IPAddress", "[0].user.basket", "[0].user.inbox", "[0].user.gender", "[0].user.interests", "[0].user.hobbies", "[0].user.profession", "[0].user.institution", "[0].user.openURL", "[0].user.place", "[0].user.spammer", "[0].user.settings", "[0].user.algorithm", "[0].user.prediction", "[0].user.mode", "[0].user.updatedBy", "[0].user.toClassify", "[0].user.reminderPassword", "[0].user.openID", "[0].user.ldapId", "[0].user.activationCode", "[0].user.remoteUserIds", "[0].user.gravatarAddress"};
	private static final String[] IGNORE2 = new String[] {"activationCode", "apiKey", "email", "homepage", "password", "passwordSalt", "realname", "date", "openURL", "gender", "place", "IPAddress", "basket", "inbox", "profession", "spammer", "settings", "hobbies", "interests", "toClassify", "updatedBy", "reminderPassword", "openID", "ldapId", "institution", "remoteUserIds", "gravatarAddress"};
	private static final String[] IGNORE3 = new String[] {"date", "user.activationCode", "user.apiKey", "user.email", "user.homepage", "user.password", "user.passwordSalt", "user.realname", "resource.scraperId", "resource.openURL", "resource.numberOfRatings", "resource.rating", "user.IPAddress", "user.basket", "user.inbox", "user.gender", "user.interests", "user.hobbies", "user.profession", "user.institution", "user.openURL", "user.place", "user.spammer", "user.confidence", "user.settings", "user.algorithm", "user.prediction", "user.mode", "user.toClassify", "user.updatedBy", "user.reminderPassword", "user.openID", "user.ldapId", "user.remoteUserIds", "user.gravatarAddress"};
	
	private static final int PORT = 41252;

	private static final Log log = LogFactory.getLog(LogicInterfaceProxyTest.class);
	
	private static final String LOGIN_USER_NAME = LogicInterfaceProxyTest.class.getSimpleName().toLowerCase();
	private static final String API_KEY = "A P I äöü K e y";
	private static Server server;
	private static String apiUrl;
	private static LogicInterfaceFactory clientLogicFactory;
	
	
	private LogicInterface clientLogic;
	private LogicInterface serverLogic;
	
	/**
	 * MultipartFilter that does not require a SpringContext
	 * @author Jens Illig
	 */
	public static class CommonsMultiPartFilter extends MultipartFilter {
		private static final MultipartResolver resolver = new CommonsMultipartResolver();
		@Override
		protected MultipartResolver lookupMultipartResolver(HttpServletRequest request) {
			return resolver;
		}
	}
	
	/**
	 * configures the server and the webapp and starts the server
	 */
	@BeforeClass
	public static void initServer() {
		initServer(RenderingFormat.XML);
	}
	
	public static void initServer(RenderingFormat renderingFormat) {
		try {
			server = new Server();
			final AbstractConnector connector = new SocketConnector();
			connector.setHost("127.0.0.1");
			connector.setPort(PORT);
			
			apiUrl = "http://localhost:" + PORT + "/api";
			server.addConnector(connector);
			final ServletContextHandler servletContext = new ServletContextHandler();
			servletContext.setContextPath("/api");
			
			final RestServlet restServlet = new RestServlet();
			final UrlRenderer urlRenderer = new UrlRenderer(apiUrl);
			restServlet.setUrlRenderer(urlRenderer);
			restServlet.setRendererFactory(new RendererFactory(urlRenderer));
			restServlet.setFileLogic(createFileLogic());
			
			try {
				final BasicAuthenticationHandler handler = new BasicAuthenticationHandler();
				handler.setLogicFactory(new MockLogicFactory());
				restServlet.setAuthenticationHandlers(Arrays.<AuthenticationHandler<?>>asList(handler));
			} catch (final Exception e) {
				throw new RuntimeException("problem while instantiating " + MockLogicFactory.class.getName(), e);
			}
			
			servletContext.addServlet(RestServlet.class, "/*").setServlet(restServlet);
			
			servletContext.addFilter(CommonsMultiPartFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
			
			server.setHandler(servletContext);
			server.start();
			connector.start();
			
			clientLogicFactory = new RestLogicFactory(apiUrl, renderingFormat);
		} catch (final Exception ex) {
			log.fatal(ex.getMessage(),ex);
			throw new RuntimeException(ex);
		}
	}

	protected static ServerFileLogic createFileLogic() {
		final ServerFileLogic fileLogic = new ServerFileLogic();
		ServerDocumentFileLogic documentLogic = new ServerDocumentFileLogic(getTmpDir());
		documentLogic.setExtensionChecker(new ListExtensionChecker(Arrays.asList("pdf", "ps", "txt")));
		fileLogic.setDocumentFileLogic(documentLogic);
		return fileLogic;
	}

	private static String getTmpDir() {
		File f;
		try {
			f = File.createTempFile("dummy", "tmp");
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		try {
			return f.getParent() + File.separator;
		} finally {
			f.delete();
		}
	}

	/**
	 * stops the servlet container after all tests have been run
	 */
	@AfterClass
	public static void shutdown() {
		try {
			server.stop();
		} catch (final Exception ex) {
			log.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex);
		}
	}
	
	private static void assertLogin() {
		assertEquals(LOGIN_USER_NAME, MockLogicFactory.getRequestedLoginName());
		assertEquals(API_KEY, MockLogicFactory.getRequestedApiKey());
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
	
	private static interface Checker<T> {
		public boolean check(T obj);
	}
	
	private static class CheckerDelegatingMatcher<T> implements IArgumentMatcher {

		private final Checker<T> checker;

		private CheckerDelegatingMatcher(Checker<T> checker) {
			this.checker = checker;
		}
		
		@SuppressWarnings("unchecked") // classcastexception is ok in testcase 
		@Override
		public boolean matches(Object argument) {
			return checker.check((T) argument);
		}

		@Override
		public void appendTo(StringBuffer buffer) {
			buffer.append("checker: " + checker);
		}
		
		/**
		 * Tells {@link EasyMock} to have the argument checked by the given Checker
		 * @param checker
		 * @return unimportant
		 */
		public static <T> T check(Checker<T> checker) {
			EasyMock.reportMatcher(new CheckerDelegatingMatcher<T>(checker));
			return null;
		}
	}
	
	
	/** IArgumentMatcher Implementation that wraps an object and compares it with another 
	 * @param <T> type of stuff to be compared */
	private static class PropertyEqualityArgumentMatcher<T> implements IArgumentMatcher {
		private final T a;
		private final String[] excludeProperties; 
		
		private PropertyEqualityArgumentMatcher(final T a, final String... excludeProperties) {
			this.a = a;
			this.excludeProperties = excludeProperties;
		}
		
		@Override
		public void appendTo(final StringBuffer arg0) {
			arg0.append("hurz");
		}

		@Override
		public boolean matches(final Object b) {
			try {
				CommonModelUtils.assertPropertyEquality(this.a, b, 5, null, excludeProperties);
			} catch (final Throwable t) {
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
	 * runs the test defined by {@link #createGroup(Group)} with certain arguments
	 */
	@Test
	public void createGroupTest() {
		createGroup(ModelUtils.getGroup());
	}
	
	@Override
	public String createGroup(final Group group) {

		/*
		 * FIXME: remove this line. It is here only, because privlevel is not included 
		 * in the XML and hence not transported to the serverLogic.
		 */
		group.setPrivlevel(null); 
		
		EasyMock.expect(serverLogic.createGroup(PropertyEqualityArgumentMatcher.eq(group, "groupId"))).andReturn(group.getName() + "-new");
		EasyMock.replay(serverLogic);
		assertEquals(group.getName() + "-new", clientLogic.createGroup(group));
		EasyMock.verify(serverLogic);
		assertLogin();
		return null;
	}
	
	/**
	 * runs the test defined by {@link #createPosts(List)} with a populated Bookmark Post as the argument
	 */
	@Test
	public void createPostTestBookmark() {
		final List<Post<?>> posts = new LinkedList<Post<?>>();
		posts.add(ModelUtils.generatePost(Bookmark.class));
		createPosts(posts);
	}
	/**
	 * runs the test defined by {@link #createPosts(List)} with a populated Publication Post as the argument
	 */
	@Test
	public void createPostTestPublication() {
		final List<Post<?>> posts = new LinkedList<Post<?>>();
		posts.add(ModelUtils.generatePost(BibTex.class));
		createPosts(posts);
	}
	
	@Override
	public List<String> createPosts(final List<Post<?>> posts) {
		final Post<?> post = posts.get(0);
		post.getUser().setName(LOGIN_USER_NAME);
				
		final List<String> singletonList = Collections.singletonList(post.getResource().getIntraHash());

		EasyMock.expect(serverLogic.createPosts(PropertyEqualityArgumentMatcher.eq(posts, IGNORE1))).andReturn(singletonList);
		EasyMock.replay(serverLogic);
		assertEquals(singletonList, clientLogic.createPosts(posts));
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
	
	@Override
	public String createUser(final User user) {

		final User eqUser = PropertyEqualityArgumentMatcher.eq(user, IGNORE2);
		
		final String userName = serverLogic.createUser(eqUser);
		
		EasyMock.expect(userName).andReturn(user.getName() + "-new");
		EasyMock.replay(serverLogic);
		assertEquals(user.getName() + "-new", clientLogic.createUser(user));
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
	
	@Override
	public void deleteGroup(final String groupName) {
		serverLogic.deleteGroup(groupName);
		EasyMock.replay(serverLogic);
		clientLogic.deleteGroup(groupName);
		EasyMock.verify(serverLogic);
		assertLogin();
	}

	/**
	 * runs the test defined by {@link #deletePosts(String, List)} with certain arguments
	 */
	@Test
	public void deletePostTest() {
		deletePosts("hurzelUserName", Collections.singletonList(ModelUtils.getBookmark().getIntraHash()));
	}
	
	@Override
	public void deletePosts(final String userName, final List<String> resourceHashes) {
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
	
	@Override
	public void deleteUser(final String userName) {
		serverLogic.deleteUser(userName);
		EasyMock.replay(serverLogic);
		clientLogic.deleteUser(userName);
		EasyMock.verify(serverLogic);
		assertLogin();
	}

	@Override
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
	
	@Override
	public Group getGroupDetails(final String groupName) {
		final Group returnedGroupExpectation = ModelUtils.getGroup();
		
		/*
		 * FIXME: remove this line. It is here only, because privlevel is not included 
		 * in the XML and hence not transported to the serverLogic.
		 */
		returnedGroupExpectation.setPrivlevel(null); 
		
		final List<User> users = new ArrayList<User>();
		users.add(ModelUtils.getUser());
		users.get(0).setName("Nr1");
		users.add(ModelUtils.getUser());
		for (final User u : users) {
			u.setApiKey(null);
			u.setPassword(null);
			final GroupMembership groupMembership = new GroupMembership();
			groupMembership.setUser(u);
			returnedGroupExpectation.getMemberships().add(groupMembership);
		}
		EasyMock.expect(serverLogic.getGroupDetails(groupName)).andReturn(returnedGroupExpectation);
		EasyMock.replay(serverLogic);
		final Group returnedGroup = clientLogic.getGroupDetails(groupName);

		CommonModelUtils.assertPropertyEquality(returnedGroupExpectation, returnedGroup, 5, Pattern.compile(".*users.*\\.(" + COMMON_USER_PROPERTIES + ")|.*\\.date|.*\\.scraperId|.*\\.openURL|.*groupId|user.*"));
		EasyMock.verify(serverLogic);
		assertLogin();
		return returnedGroup;
	}
	
	/**
	 * runs the test defined by {@link #getGroups(boolean, int, int)} with certain arguments
	 */
	@Test
	public void getGroupsTest() {
		getGroups(false, 64, 129);
	}
	
	@Override
	public List<Group> getGroups(boolean pending, final int start, final int end) {
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
		
		EasyMock.expect(serverLogic.getGroups(false, start, end)).andReturn(expectedList);
		EasyMock.replay(serverLogic);
		final List<Group> returnedGroups = clientLogic.getGroups(false,start, end);
		CommonModelUtils.assertPropertyEquality(expectedList, returnedGroups, 3, Pattern.compile(".*\\.groupId"));
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
	
	@Override
	@SuppressWarnings("unchecked")
	public Post<? extends org.bibsonomy.model.Resource> getPostDetails(final String resourceHash, final String userName) {
		final Post<BibTex> expectedPublicationPost = ModelUtils.generatePost(BibTex.class);
		final Post<Bookmark> expectedBookmarkPost = ModelUtils.generatePost(Bookmark.class);
		
		try {
			EasyMock.expect(serverLogic.getPostDetails(resourceHash, userName)).andReturn((Post) expectedPublicationPost);
		} catch (final ObjectNotFoundException ex) {
			// ignore
		} catch (final ResourceMovedException ex) {
			// ignore
		}
		try {
			EasyMock.expect(serverLogic.getPostDetails(resourceHash, userName)).andReturn((Post) expectedBookmarkPost);
		} catch (final ObjectNotFoundException ex) {
			// ignore
		} catch (final ResourceMovedException ex) {
			// ignore
		}
		EasyMock.replay(serverLogic);
		
		Post<? extends org.bibsonomy.model.Resource> returnedPublicationPost = null;
		try {
			returnedPublicationPost = clientLogic.getPostDetails(resourceHash,userName);
		} catch (final ObjectNotFoundException ex) {
			// ignore
		} catch (final ResourceMovedException ex) {
			// ignore
		}
		CommonModelUtils.assertPropertyEquality(expectedPublicationPost, returnedPublicationPost, 5, null, IGNORE3);
		Post<? extends org.bibsonomy.model.Resource> returnedBookmarkPost = null;
		try {
			returnedBookmarkPost = clientLogic.getPostDetails(resourceHash,userName);
		} catch (final ObjectNotFoundException ex) {
			// ignore
		} catch (final ResourceMovedException ex) {
			// ignore
		}
		CommonModelUtils.assertPropertyEquality(expectedBookmarkPost, returnedBookmarkPost, 5, null, IGNORE3);
		EasyMock.verify(serverLogic);
		assertLogin();
		return returnedPublicationPost;
	}
	
	/**
	 * runs the test defined by {@link #getPosts(Class, GroupingEntity, String, List, String, String, Set, Order, Date, Date, int, int)} with arguments as used for the getBookmarkByTagName query
	 */
	@Test
	public void getPostsTestBookmarkByTag() {
		getPosts(Bookmark.class, GroupingEntity.ALL, null, Arrays.asList("bla", "blub"), null, null, SearchType.LOCAL,null,  null /* must be null because order is inferred and not transmitted */, null, null, 7, 1264);
	}
	
	/**
	 * runs the test defined by {@link #getPosts(Class, GroupingEntity, String, List, String, String, Set, Order, Date, Date, int, int)} with arguments as used for the getPublicationForGroupAndTag query
	 */
	@Test
	public void getPostsTestPublicationByGroupAndTag() {
		getPosts(BibTex.class, GroupingEntity.GROUP, "testGroup", Arrays.asList("blub", "bla"), null, null,SearchType.LOCAL, null, null, null, null, 0, 1);
	}
	
	/**
	 * tests whether tags with umlauts can be queried correctly
	 */
	@Test
	public void getPostsTestPublicationByTagWithUmlaut() {
		getPosts(BibTex.class, GroupingEntity.ALL, null, Arrays.asList("blüb"), null, null,SearchType.LOCAL, null, null, null, null, 0, 1);
	}
	
	/**
	 * runs the test defined by {@link #getPosts(Class, GroupingEntity, String, List, String, String, Set, Order, Date, Date, int, int)} with arguments as used for the getPublicationByHashForUser query 
	 */
	@Test
	public void getPostsTestPublicationByUserAndHash() {
		getPosts(BibTex.class, GroupingEntity.USER, "testUser", new ArrayList<String>(0), ModelUtils.getBibTex().getIntraHash(), null,SearchType.LOCAL, null, null, null, null, 0, 5);
	}
	
	@Test
	public void getPostsTestWithSearchAndOrder() {
		getPosts(BibTex.class, GroupingEntity.USER, "testUser", new ArrayList<String>(0), ModelUtils.getBibTex().getIntraHash(), "search",SearchType.LOCAL, null, Order.FOLKRANK, null, null, 0, 5);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T extends org.bibsonomy.model.Resource> List<Post<T>> getPosts(final Class<T> resourceType, final GroupingEntity grouping, final String groupingName, final List<String> tags, final String hash, final String search, final SearchType searchType, final Set<Filter> filters, final Order order, final Date startDate, final Date endDate, final int start, final int end) {
		final List<Post<T>> expectedPosts = new ArrayList<Post<T>>();
		expectedPosts.add(ModelUtils.generatePost(resourceType));
		expectedPosts.get(0).setDescription("erstes");
		expectedPosts.add(ModelUtils.generatePost(resourceType));
		if (resourceType == org.bibsonomy.model.Resource.class) {
			expectedPosts.add( (Post) ModelUtils.generatePost(Bookmark.class));
			expectedPosts.add( (Post) ModelUtils.generatePost(BibTex.class));
		}
		
		EasyMock.expect(serverLogic.getPosts(resourceType, grouping, groupingName, tags, hash, search,searchType, filters, order, null, null, start, end)).andReturn(expectedPosts);
		EasyMock.replay(serverLogic);

		final List<Post<T>> returnedPosts = clientLogic.getPosts(resourceType, grouping, groupingName, tags, hash, search, searchType, filters, order, null, null, start, end);
		CommonModelUtils.assertPropertyEquality(expectedPosts, returnedPosts, 5, Pattern.compile(".*\\.user\\.(" + COMMON_USER_PROPERTIES + "|confidence|activationCode|reminderPassword|openID|ldapId|remoteUserIds|prediction|algorithm|mode)|.*\\.date|.*\\.scraperId|.*\\.openURL|.*\\.numberOfRatings|.*\\.rating"));
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
	
	@Override
	public Tag getTagDetails(final String tagName) {
		final Tag expected = ModelUtils.getTag();		
		EasyMock.expect(serverLogic.getTagDetails(tagName)).andReturn(expected);
		EasyMock.replay(serverLogic);
		
		final Tag returned = clientLogic.getTagDetails(tagName);
		CommonModelUtils.assertPropertyEquality(expected, returned, 3, Pattern.compile("(.*\\.)?(id|stem)"));
		EasyMock.verify(serverLogic);
		assertLogin();
		return returned;
	}

	
	/**
	 * runs the test defined by {@link #getTags(Class, GroupingEntity, String, String, List, String, Order, int, int, String, TagSimilarity)} with certain arguments
	 */
	@Test
	public void getTagsTest() {
		getTags(org.bibsonomy.model.Resource.class, GroupingEntity.GROUP, "testGroup", null, null, null, "regex", null, null, null, null, 4, 22);
	}
	
	
	@Override
	public List<Tag> getTags(final Class<? extends Resource> resourceType, final GroupingEntity grouping, final String groupingName, final List<String> tags, final String hash, final String search, final String regex, final TagSimilarity relation, final Order order, final Date startDate, final Date endDate, final int start, final int end) {
		final List<Tag> expected = ModelUtils.buildTagList(3, "testPrefix", 1);		
		EasyMock.expect(serverLogic.getTags(resourceType, grouping, groupingName, tags, null, null, regex, null, order, null, null, start, end)).andReturn(expected);
		EasyMock.replay(serverLogic);
		
		final List<Tag> returned = clientLogic.getTags(resourceType, grouping, groupingName, tags, null, null, regex, null, order, null, null, start, end);
		CommonModelUtils.assertPropertyEquality(expected, returned, 5, Pattern.compile("(.*\\.)?(id|stem)"));
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
	
	@Override
	public User getUserDetails(final String userName) {
		final User expected = ModelUtils.getUser();		
		EasyMock.expect(serverLogic.getUserDetails(userName)).andReturn(expected);
		EasyMock.replay(serverLogic);
		
		final User returned = clientLogic.getUserDetails(userName);
		CommonModelUtils.assertPropertyEquality(expected, returned, 3, null, IGNORE2);
		EasyMock.verify(serverLogic);
		assertLogin();
		return returned;
	}

	/**
	 * runs the test defined by {@link #updateGroup(Group, GroupUpdateOperation)} with certain group argument
	 */
	@Test
	public void updateGroupTest() {
		updateGroup(ModelUtils.getGroup(), GroupUpdateOperation.UPDATE_ALL, null);
	}
	
	/**
	 * runs the test to add a user to a group 
	 */	
	@Test
	public void addUserToGroupTest() {
		final Group group = new Group("groupName");
		final GroupMembership membership = new GroupMembership(new User("testUser1"), GroupRole.USER, false);
		this.updateGroup(group, GroupUpdateOperation.ADD_MEMBER, membership);
	}
	
	/**
	 * runs the test defined by {@link #deleteUserFromGroup(String, String)} with certain arguments
	 */
	@Test
	public void deleteUserFromGroupTest() {
		final Group group = new Group("grooouuup!");
		final GroupMembership membership = new GroupMembership();
		membership.setUser(new User("userTest"));
		
		this.updateGroup(group, GroupUpdateOperation.REMOVE_MEMBER, membership);
	}
	
	@Override
	public String updateGroup(final Group group, final GroupUpdateOperation operation, GroupMembership membership) {
		String groupName = group.getName();
		switch (operation) {
		case ADD_MEMBER:
			EasyMock.expect(serverLogic.updateGroup(PropertyEqualityArgumentMatcher.eq(group, "groupId"),
					PropertyEqualityArgumentMatcher.eq(operation),
					PropertyEqualityArgumentMatcher.eq(membership))).andReturn("OK");
			EasyMock.replay(serverLogic);
			assertEquals("OK", clientLogic.updateGroup(group, operation, membership));
			EasyMock.verify(serverLogic);
			assertLogin();
			break;
		case REMOVE_MEMBER:
			EasyMock.expect(serverLogic.updateGroup(PropertyEqualityArgumentMatcher.eq(group, "groupId"),
					PropertyEqualityArgumentMatcher.eq(operation),
					PropertyEqualityArgumentMatcher.eq(membership))).andReturn(groupName);
			EasyMock.replay(serverLogic);
			assertEquals("OK", clientLogic.updateGroup(group, operation, membership));
			EasyMock.verify(serverLogic);
			assertLogin();
			break;

		default:
			/*
			 * FIXME: remove this line. It is here only, because privlevel is not included 
			 * in the XML and hence not transported to the serverLogic.
			 */
			group.setPrivlevel(null); 
			
			EasyMock.expect(serverLogic.updateGroup(PropertyEqualityArgumentMatcher.eq(group, "groupId"),
					PropertyEqualityArgumentMatcher.eq(operation, ""),
					PropertyEqualityArgumentMatcher.eq(membership))).andReturn(groupName + "-new");
			EasyMock.replay(serverLogic);
			assertEquals(groupName + "-new", clientLogic.updateGroup(group, operation, null));
			EasyMock.verify(serverLogic);
			assertLogin();
			break;
		}
		
		return null;
	}

	/**
	 * runs the test defined by {@link #updatePosts(List, PostUpdateOperation)} with a fully populated Publication Post as argument
	 */
	@Test
	public void updatePostTestPublication() {
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
	
	@Override
	public List<String> updatePosts(final List<Post<?>> posts, final PostUpdateOperation operation) {
		final Post<?> post = posts.get(0);
		post.getUser().setName(LOGIN_USER_NAME);
		
		final List<String> singletonList = Collections.singletonList(post.getResource().getIntraHash());
		
		EasyMock.expect(serverLogic.updatePosts(PropertyEqualityArgumentMatcher.eq(posts, IGNORE1), PropertyEqualityArgumentMatcher.eq(operation, ""))).andReturn(singletonList);
		EasyMock.replay(serverLogic);
		assertEquals(singletonList, clientLogic.updatePosts(posts, operation));
		EasyMock.verify(serverLogic);
		assertLogin();
		return null;
		
	}

	/**
	 * runs the test defined by {@link #updateUser(User, UserUpdateOperation)} with a certain argument
	 */
	@Test
	public void updateUserTest() {
		updateUser(ModelUtils.getUser(), UserUpdateOperation.UPDATE_ALL);
	}
	
	@Override
	public String updateUser(final User user, final UserUpdateOperation operation) {
		EasyMock.expect(serverLogic.createUser(PropertyEqualityArgumentMatcher.eq(user, IGNORE2))).andReturn("rVal");
		EasyMock.replay(serverLogic);
		assertEquals("rVal", clientLogic.createUser(user));
		EasyMock.verify(serverLogic);
		assertLogin();
		return null;
	}
	
	/**
	 * Test whether createDocument is passed through
	 */
	@Test
	public void testCreateDocument() {
		Document doc = new Document();
		
		File tmpData = null;
		try {
			tmpData = File.createTempFile(getClass().getName() + ".testCreateDocument", ".txt");
			byte[] data = "test\ndata\n".getBytes();
			
			FileUtils.writeByteArrayToFile(tmpData, data);
			
			doc.setFile(tmpData);
			doc.setFileName(tmpData.getName());
			doc.setMd5hash(HashUtils.getMD5Hash(data));
			
			createDocument(doc, "resHash");
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		} finally {
			if (tmpData != null) {
				tmpData.delete();
			}
		}
	}

	@Override
	public String createDocument(final Document doc, final String resourceHash) {
		EasyMock.expect(serverLogic.createDocument(CheckerDelegatingMatcher.check(new Checker<Document>() {
			@Override
			public boolean check(Document obj) {
				assertEquals(doc.getFileName(), obj.getFileName());
				assertEquals(doc.getMd5hash(), obj.getMd5hash());
				byte[] sent;
				byte[] received;
				try {
					sent = FileUtils.readFileToByteArray(doc.getFile());
					received = FileUtils.readFileToByteArray(obj.getFile());
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
				assertEquals(Arrays.toString(sent), Arrays.toString(received));
				return true;
			}
			
		}), EasyMock.eq(resourceHash))).andReturn("rVal");
		
		EasyMock.replay(serverLogic);
		assertEquals("rVal", clientLogic.createDocument(doc, resourceHash));
		EasyMock.verify(serverLogic);
		return null;
	}

	@Test
	public void createConceptTest() {
		createConcept(ModelUtils.getTag(), GroupingEntity.USER, "testUser");
	}
	
	@Override
	public String createConcept(final Tag concept, final GroupingEntity grouping, final String groupingName) {
		EasyMock.expect(serverLogic.createConcept(CheckerDelegatingMatcher.check(new Checker<Tag>() {
			@Override
			public boolean check(Tag obj) {
				assertEquals(concept.getName(), obj.getName());
				assertNotNull(obj.getSubTags());
				assertEquals(concept.getSubTags().size(), obj.getSubTags().size());
				for (int i = 0; i < concept.getSubTags().size(); ++i) {
					final Tag origSubTag = concept.getSubTags().get(i);
					final Tag foundSubTag = obj.getSubTags().get(i);
					assertEquals(origSubTag.getName(), foundSubTag.getName());
					assertNotNull(foundSubTag.getSuperTags());
					assertEquals(origSubTag.getSuperTags().size(), foundSubTag.getSuperTags().size());
					for (int x = 0; x < origSubTag.getSuperTags().size(); ++x) {
						assertEquals(origSubTag.getSuperTags().get(x).getName(), foundSubTag.getSuperTags().get(x).getName());
					}
				}
				assertNotNull(obj.getSuperTags());
				assertEquals(concept.getSuperTags().size(), obj.getSuperTags().size());
				for (int i = 0; i < concept.getSuperTags().size(); ++i) {
					final Tag origSuperTag = concept.getSuperTags().get(i);
					final Tag foundSuperTag = obj.getSuperTags().get(i);
					assertEquals(origSuperTag.getName(), foundSuperTag.getName());
					assertNotNull(foundSuperTag.getSubTags());
					assertEquals(origSuperTag.getSubTags().size(), foundSuperTag.getSubTags().size());
					for (int x = 0; x < origSuperTag.getSubTags().size(); ++x) {
						assertEquals(origSuperTag.getSubTags().get(x).getName(), foundSuperTag.getSubTags().get(x).getName());
					}
				}
				return true;
			}
			
		}), EasyMock.eq(grouping), EasyMock.eq(groupingName))).andReturn(concept.getName());;
		EasyMock.replay(serverLogic);
		assertEquals(concept.getName(), clientLogic.createConcept(concept, grouping, groupingName));
		EasyMock.verify(serverLogic);
		return concept.getName();
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
	
	@Override
	public List<User> getUsers(final Class<? extends org.bibsonomy.model.Resource> resourceType, final GroupingEntity grouping, final String groupingName, final List<String> tags, final String hash, final Order order, final UserRelation relation, final String search, final int start, final int end) {
		final List<User> expected = new ArrayList<User>(2);
		expected.add(ModelUtils.getUser());
		expected.get(0).setName("Nr1");
		expected.add(ModelUtils.getUser());
		expected.get(1).setName("Nr2");
		EasyMock.expect(serverLogic.getUsers(resourceType, grouping, groupingName, tags, hash, order, relation, search, start, end)).andReturn(expected);
		EasyMock.replay(serverLogic);
		final List<User> returned = clientLogic.getUsers(resourceType, grouping, groupingName, tags, hash, order, relation, search, start, end);
		CommonModelUtils.assertPropertyEquality(expected, returned, 5, Pattern.compile(       ".*\\.(" + COMMON_USER_PROPERTIES + "|activationCode|reminderPassword|openID|ldapId|remoteUserIds)"));
		EasyMock.verify(serverLogic);
		assertLogin();
		return returned;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterface#getTags(java.lang.Class, org.bibsonomy.common.enums.GroupingEntity, java.lang.String, java.util.List, java.lang.String, java.lang.String, org.bibsonomy.common.enums.SearchType, java.lang.String, org.bibsonomy.common.enums.TagSimilarity, org.bibsonomy.model.enums.Order, java.util.Date, java.util.Date, int, int)
	 */
	@Override
	public List<Tag> getTags(Class<? extends Resource> resourceType,
			GroupingEntity grouping, String groupingName, List<String> tags,
			String hash, String search, SearchType searchType, String regex,
			TagSimilarity relation, Order order, Date startDate, Date endDate,
			int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}
}