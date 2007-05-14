package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.model.User;
import org.junit.Test;

/**
 * Tests related to users.
 *
 * @author Miranda Grahl
 * @version $Id$
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

	@Test
	public void getUsersOfGroupPublic() {
		final List<User> users = this.userDb.getUsersOfGroupPublic(this.userParam, this.dbSession);
		assertEquals(13, users.size());
	}

	@Test
	public void getUsersOfGroupHidden() {
		final List<User> users = this.userDb.getUsersOfGroupPrivate(this.userParam, this.dbSession);
		assertEquals(1, users.size());
	}


//	@SuppressWarnings("unchecked")
//	public void testGetUsersByDate() {
//		try {
//			final ByUser bu = this.getDefaultByUser();
///*
// * bekomme Liste von Usern zurück
// */
//			/*
//			 * in XML ist id eingebettet
//			 */
//			final List<User> users = this.sqlMap.queryForList("getByUser", bu);
//
//			for (final User user : users) {
//				System.out.println("name              = " + user.getName());
//				System.out.println("real name         = " + user.getRealname());
//				System.out.println("email             = " + user.getEmail());
//				System.out.println("homepage          = " + user.getHomepage());
//				System.out.println("registration date = " + user.getRegistrationDate());
//				System.out.println("-------------------");
//			}
//		} catch (final SQLException ex) {
//			ex.printStackTrace();
//		}
//	}

	@Test
	public void testInsertUser() {
		/*
		try {
			final User user = this.getDefaultUser();

			this.sqlMap.insert("insertUser", user);

		} catch (final SQLException ex) {
			ex.printStackTrace();
		}
		*/
	}
}