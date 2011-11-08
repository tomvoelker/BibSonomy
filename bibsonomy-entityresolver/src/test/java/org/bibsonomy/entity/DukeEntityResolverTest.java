package org.bibsonomy.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;

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
		newUser.setRealname("T. User");
		users.add(newUser);
		newUser = new User();
		newUser.setRealname("Test Group");
		users.add(newUser);
		newUser = new User();
		newUser.setRealname("Test User");
		users.add(newUser);
		UserDataSource newEntries = new UserDataSource(users);
		Collection<DataSource> linkGroup = new ArrayList<DataSource>();
		linkGroup.add(newEntries);

		proc.buildIndex(config.getDataSources(), 10);
		proc.linkRecords(linkGroup, false);
		
		for (Map.Entry<String,SortedSet<UserMatch>> match : matcher.getMatching().entrySet()) {
			String extId = match.getKey();
			System.out.println("Matching for user '"+extId+"':");
			for (UserMatch other : match.getValue()) {
				System.out.println("\t"+other.getId()+" ["+other.getConfidence()+"]");
			}
		}
		
		proc.close();
	}
}
