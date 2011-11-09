package org.bibsonomy.entity;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import no.priv.garshol.duke.ConfigLoader;
import no.priv.garshol.duke.Configuration;
import no.priv.garshol.duke.DataSource;
import no.priv.garshol.duke.Processor;

import org.bibsonomy.database.testutil.JNDIBinder;
import org.bibsonomy.entity.datasource.UserDataSource;
import org.bibsonomy.entity.matcher.UserEntityMatcher;
import org.bibsonomy.entity.matcher.UserEntityMatcher.UserMatch;
import org.bibsonomy.model.User;
import org.bibsonomy.testutil.TestDatabaseLoader;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DukeEntityResolverTest  {
	/**
	 * Initializes the test database.
	 */
	@Before
	public void setupDuke() {
	}
	/**
	 * Initializes the test database.
	 */
	@BeforeClass
	public static void initDatabase() {
		// bind datasource access via JNDI
		JNDIBinder.bind();

		TestDatabaseLoader.getInstance().load();
	}
	
	/**
	 * unbinds jndi
	 */
	@AfterClass
	public static void unbind() {
		JNDIBinder.unbind();
	}
	
	/**
	 * tests basic user name matching
	 * @throws Exception
	 */
	@Test
	public void userLinkageTest() throws Exception {
		Configuration config = ConfigLoader.load("classpath:UsernameResolver.xml");
		config.setPath("/tmp/duke_user_idx");
		Processor proc = new Processor(config);
		//proc.addMatchListener(new PrintMatchListener(true, true, true));
		UserEntityMatcher matcher = new UserEntityMatcher();
		proc.addMatchListener(matcher);
		

		Collection<User> users = new ArrayList<User>();
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
		UserDataSource newEntries = new UserDataSource(users);
		Collection<DataSource> linkGroup = new ArrayList<DataSource>();
		linkGroup.add(newEntries);

		proc.index(config.getDataSources(), 10);
		proc.linkRecords(linkGroup);
		
		SortedSet<UserMatch> match1 = matcher.getMatching().get("Test User"); 
		SortedSet<UserMatch> match2 = matcher.getMatching().get("Test User 1"); 
		SortedSet<UserMatch> match3 = matcher.getMatching().get("Test User 2");
		
		assertTrue(match1.size()>0);
		assertTrue(match2.size()>0);
		assertTrue(match3.size()>0);
		
		assertEquals("Test User 1", match1.iterator().next().getId());
		assertEquals("Test User 1", match2.iterator().next().getId());
		assertEquals("Test User 2", match3.iterator().next().getId());
		
		proc.close();
	}
}
