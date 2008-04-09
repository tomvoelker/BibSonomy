package org.bibsonomy.database.managers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.model.User;
import org.junit.Test;

/**
 * @author Robert Jaeschke
 * @version $Id$
 */
public class PermissionDatabaseManagerTest extends AbstractDatabaseManagerTest {

	/**
	 * This method must not throw an exception, because users with role ADMIN
	 * should have admin access.
	 * 
	 * @throws ValidationException
	 */
	@Test
	public void ensureAdminAccess() throws ValidationException {
		User user = new User();
		user.setName("rjaeschke");
		user.setRole(Role.ADMIN);
		// should work
		this.permissionDb.ensureAdminAccess(user);

		for (final String name : new String[] { "", " ", null }) {
			user = new User();
			user.setName(name);

			// This must throw an exception, because users with role DEFAULT
			// must not have admin access.
			try {
				user.setRole(Role.DEFAULT);
				this.permissionDb.ensureAdminAccess(user);
				fail("should throw an exception");
			} catch (ValidationException ignore) {
			}

			// This must throw an exception, because users without a name
			// (independent of their role) must not have admin access.
			try {
				user.setRole(Role.ADMIN);
				this.permissionDb.ensureAdminAccess(user);
				fail("should throw an exception");
			} catch (ValidationException ignore) {
			}
		}
	}

	/**
	 * tests exceedsMaxmimumSize
	 */
	@Test
	public void exceedsMaxmimumSize() {
		final List<String> tags = new ArrayList<String>();

		for (int i = 0; i < 9; i++) {
			tags.add("tag"+ i);
			assertFalse(this.permissionDb.exceedsMaxmimumSize(tags));
		}
		for (int i = 10; i < 42; i++) {
			tags.add("tag"+ i);
			assertTrue(this.permissionDb.exceedsMaxmimumSize(tags));
		}
	}
}