package org.bibsonomy.community.database;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.community.model.User;
import org.bibsonomy.community.util.JNDITestDatabaseBinder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for recommender's DBAccess class
 * @author fei
 * @version $Id$
 */
public class UserManagerTest {
	private static final Log log = LogFactory.getLog(UserManagerTest.class);
	
	UserSettingsManager userManager;
	CommunityManager communityManager;
	
	@Before
	public void setUp() {
		// bind datasource access via JNDI
		JNDITestDatabaseBinder.bind();
		communityManager = CommunityManager.getInstance();
		userManager      = UserSettingsManager.getInstance();
	}
	
	@After
	public void tearDown() {
		JNDITestDatabaseBinder.unbind();
	}

	
	/**
	 * Test adding new algorithm
	 * @throws Exception 
	 */
	@Test
	public void testBibTexPosts() throws Exception {
		User user = new User("testUser");
		user.setAffiliation(17, 23, 0.3412);
		user.setAffiliation(17, 42, 0.9918);
		user.setAffiliation(17, 55, 1.0);
		
		userManager.setUserAffiliation(user);
		
		User user2 = new User("testUser");
		userManager.fillUserAffiliation(user2);
	}	


	
	//------------------------------------------------------------------------
	// private helpers
	//------------------------------------------------------------------------
}
