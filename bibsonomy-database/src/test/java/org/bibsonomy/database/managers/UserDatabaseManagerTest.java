package org.bibsonomy.database.managers;



import org.junit.Test;

public class UserDatabaseManagerTest extends AbstractDatabaseManagerTest {
	
	@Test
	public void getUserDetails() {
		this.userDb.getUserDetails(this.userParam);
	}	

	@Test
	public void getUsersOfSystem() {
		this.userDb.getUsersOfSystem(this.userParam);
	}	
	
	@Test
	public void getPrivlevelOfUser() {
		this.userDb.getPrivlevelOfUser(this.userParam);
	}	
	
	@Test
	public void getUsersOfGroupPublic() {
		this.userDb.getUsersOfGroupPublic(this.userParam);
	}	
	@Test
	public void getUsersOfGroupHidden() {
		this.userDb.getUsersOfGroupPrivate(this.userParam);
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