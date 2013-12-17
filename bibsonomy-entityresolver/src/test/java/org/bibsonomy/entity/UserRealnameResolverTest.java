package org.bibsonomy.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.bibsonomy.model.User;
import org.bibsonomy.testutil.TestDatabaseLoader;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author fei
  */
public class UserRealnameResolverTest {
	
	private static UserRealnameResolver resolver;
	
	/**
	 * Initializes the test database.
	 */
	@BeforeClass
	public static void initDatabase() {
		TestDatabaseLoader.getInstance().load();
		
		final ApplicationContext context = new ClassPathXmlApplicationContext("TestEntityResolverContext.xml");
		resolver = context.getBean(UserRealnameResolver.class);	
	}
	
	/**
	 * builds index
	 */
	@Before
	public void buildIndex() {
		resolver.buildIndex();
	}
	
	/**
	 * tests basic user name matching
	 * @throws Exception
	 */
	@Test
	public void userLinkageTest() throws Exception {
		final Collection<User> users = new ArrayList<User>();
		User newUser = new User();
		newUser.setRealname("Test User 1");
		newUser.setHomepage(new URL("http://www.bibsonomy.org/user/testuser"));
		users.add(newUser);
		newUser = new User();
		newUser.setRealname("Test User 2");
		newUser.setHomepage(new URL("http://www.biblicious.org/user/testuser"));
		users.add(newUser);
		newUser = new User();
		newUser.setRealname("Test User");
		newUser.setHomepage(new URL("http://www.bibsonomy.org/user/testuser1"));
		newUser.setPlace("test-place");
		users.add(newUser);
		newUser = new User();
		newUser.setRealname("Test Group");
		users.add(newUser);
		
		final Map<String, Collection<User>> resolvedUsers = resolver.resolveUsers(users);
		
		final Collection<User> match1 = resolvedUsers.get("Test User"); 
		final Collection<User> match2 = resolvedUsers.get("Test User 1"); 
		final Collection<User> match3 = resolvedUsers.get("Test User 2");
		
		assertTrue(match1.size() > 0);
		assertTrue(match2.size() > 0);
		assertTrue(match3.size() > 0);
		
		assertEquals("Test User 1", match1.iterator().next().getRealname());
		assertEquals("Test User 1", match2.iterator().next().getRealname());
		assertEquals("Test User 2", match3.iterator().next().getRealname());
	}
}
