package org.bibsonomy.database.managers;

import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.model.User;
import org.junit.Test;


/**
 * @author rja
 * @version $Id$
 */
public class PermissionDatabaseManagerTest extends AbstractDatabaseManagerTest {
	

	/** This method must not throw an exception, because users with role
	 * ADMIN should have admin access. 
	 * @throws ValidationException
	 */
	@Test
	public void ensureAdminAccessSuccess () throws ValidationException {
		User loginUser = new User();
		loginUser.setName("rjaeschke");
		loginUser.setRole(Role.ADMIN);
		
		// should work
		this.permissionDb.ensureAdminAccess(loginUser);
	}
	
	/**
	 * This method must throw an exception, because users with role
	 * DEFAULT must not have admin access.
	 */
	@Test(expected = ValidationException.class)
	public void ensureAdminAccessFailure() {
		User loginUser = new User();
		loginUser.setRole(Role.DEFAULT);
		
		// should throw an exception
		this.permissionDb.ensureAdminAccess(loginUser);
	}
	
	/**
	 * This method must throw an exception, because users without a
	 * name (independent of their role) must not have admin access.
	 */
	@Test(expected = ValidationException.class)
	public void ensureAdminAccessFailure2() {
		User loginUser = new User();
		loginUser.setRole(Role.ADMIN);
		
		// should throw an exception
		this.permissionDb.ensureAdminAccess(loginUser);
	}
	

}
