package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.UserUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests related to users.
 * 
 * @author Miranda Grahl
 * @version $Id: UserDatabaseManagerTest.java,v 1.11 2007/05/19 23:59:50 jillig
 *          Exp $
 */
public class UserDatabaseManagerTest extends AbstractDatabaseManagerTest {

	@Test
	public void getAllUsers() {
		final List<User> users = this.userDb.getAllUsers(this.userParam, this.dbSession);
		assertEquals(1568, users.size());
	}

	@Test
	public void getUserDetails() {
		this.userDb.getUserDetails(this.userParam, this.dbSession);
	}

	@Test
	public void getPrivlevelOfUser() {
		assertEquals(GroupID.GROUP_PUBLIC.getId(), this.userDb.getPrivlevelOfUser(this.userParam, this.dbSession));
	}

	public void getUserNamesOfGroupId() {
		final List<String> users = this.userDb.getUserNamesByGroupId(GroupID.GROUP_KDE.getId(), dbSession);
		final String[] kdeUsers = new String[] { "kde", "schmitz", "chs", "jaeschke", "stumme", "gst", "sfi", "finis", "rja", "aho", "hotho", "grahl", "beate" };
		Assert.assertTrue(users.containsAll(Arrays.asList(kdeUsers)));
		Assert.assertEquals(kdeUsers.length, users.size());
	}

	// TODO: implement and @Test
	public void getUsersOfGroupPublic() {
		final List<User> users = this.userDb.getUsersOfGroupPublic(this.userParam, this.dbSession);
		assertEquals(13, users.size());
	}

	@Test
	public void getUsersOfGroupHidden() {
		final List<User> users = this.userDb.getUsersOfGroupPrivate(this.userParam, this.dbSession);
		assertEquals(1, users.size());
	}

	// @SuppressWarnings("unchecked")
	// public void testGetUsersByDate() {
	// try {
	// final ByUser bu = this.getDefaultByUser();
	// /*
	// * bekomme Liste von Usern zurück
	// */
	// /*
	// * in XML ist id eingebettet
	// */
	// final List<User> users = this.sqlMap.queryForList("getByUser", bu);
	//
	// for (final User user : users) {
	// System.out.println("name = " + user.getName());
	// System.out.println("real name = " + user.getRealname());
	// System.out.println("email = " + user.getEmail());
	// System.out.println("homepage = " + user.getHomepage());
	// System.out.println("registration date = " + user.getRegistrationDate());
	// System.out.println("-------------------");
	// }
	// } catch (final SQLException ex) {
	// ex.printStackTrace();
	// }
	// }

	@Test
	public void testInsertUser() {
		
		UserParam param=new UserParam();
		URL url;
		try {
			url = new URL("http://www.db.de");
			param.setUserName("neuerUser");
			param.setEmail("mgr@cs.uni-kassel.de");
			param.setHomepage(url);
			param.setPassword("dhdhd");
			param.setRealname("mira");
			this.userDb.insertUser(param,this.dbSession);
			
			User user =this.userDb.getUserDetails(param,this.dbSession);
			
			assertEquals(user.getName(), "neuerUser");
			assertEquals(user.getEmail(), "mgr@cs.uni-kassel.de");
			assertEquals(user.getHomepage(), url);
			assertEquals(user.getRealname(), "mira");
			assertEquals(user.getApiKey(), param.getApiKey());
			
			
		} catch (MalformedURLException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		
		
		
		
		
		/*
		 * try { final User user = this.getDefaultUser();
		 * 
		 * this.sqlMap.insert("insertUser", user);
		 *  } catch (final SQLException ex) { ex.printStackTrace(); }
		 */
	}

	/*
	 * @Test public void getApiKeyForUser() {
	 * this.userDb.getApiKeyForUser(this.userParam, this.dbSession); }
	 */

	@Test
	public void getApiKeyForUser() {

		for (int i = 0; i < 10; i++) {
			String apiKey = UserUtils.generateApiKey();

			//System.out.println("ApiKey: " + apiKey);

		}
	}

	@Test
	public void updateApiKeyForUser() {
		
		this.resetParameters();		
		this.userDb.updateApiKeyForUser(this.userParam, this.dbSession);
				
		assertEquals(this.userParam.getApiKey(), this.userDb.getApiKeyForUser(this.userParam, this.dbSession));
				
	}
	
	@Test
	
	public void generateApiKeysForAllUsers() {
		this.userDb.generateApiKeysForAllUsers(this.dbSession);
	}

}