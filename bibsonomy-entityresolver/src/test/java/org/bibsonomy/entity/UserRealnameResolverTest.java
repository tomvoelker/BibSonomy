package org.bibsonomy.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.bibsonomy.model.User;
import org.bibsonomy.testutil.TestDatabaseLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author fei
 */
public class UserRealnameResolverTest {
	private UserRealnameResolver resolver;
	
	/**
	 * builds index
	 * @throws IOException
	 */
	@Before
	public void buildIndex() throws IOException {
		final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("TestEntityResolverContext.xml");
		final String path = context.getBean("path", String.class);
		context.close();
		FileUtils.deleteDirectory(new File(path));
		TestDatabaseLoader.getInstance().load();
		
		context.refresh();
		
		resolver = context.getBean(UserRealnameResolver.class);
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
		
		assertNotNull(match1);
		assertNotNull(match2);
		assertNotNull(match3);
		
		assertTrue(match1.size() > 0);
		assertTrue(match2.size() > 0);
		assertTrue(match3.size() > 0);
		
		assertEquals("Test User 1", match1.iterator().next().getRealname());
		assertEquals("Test User 1", match2.iterator().next().getRealname());
		assertEquals("Test User 2", match3.iterator().next().getRealname());
	}
	
	@After
	public void closeResolver() throws IOException {
		this.resolver.close();
	}
}
