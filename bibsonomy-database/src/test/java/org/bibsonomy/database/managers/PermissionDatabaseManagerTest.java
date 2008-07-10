package org.bibsonomy.database.managers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.testutil.ParamUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Robert Jäschke
 * @version $Id$
 */
public class PermissionDatabaseManagerTest extends AbstractDatabaseManagerTest {

	/**
	 * tests checkStartEnd
	 */
	@Test
	public void checkStartEnd() {
		// ok
		for (int i = 0; i <= 1000; i++) {
			try {
				this.permissionDb.checkStartEnd(0, i, "test");
			} catch (ValidationException ignore) {
				fail("no exception expected");
			}
		}
		// not ok
		for (int i = 1001; i < 10000; i++) {
			try {
				this.permissionDb.checkStartEnd(0, i, "test");
				fail("expected exception");
			} catch (ValidationException ignore) {
			}
		}
	}

	/**
	 * tests ensureWriteAccess
	 */
	@Test
	public void ensureWriteAccess() {
		final Post<Resource> post = new Post<Resource>();
		post.setUser(new User("testuser1"));
		this.permissionDb.ensureWriteAccess(post, new User("testuser1"));
		try {
			this.permissionDb.ensureWriteAccess(post, new User("testuser2"));
			fail("expected exception");
		} catch (ValidationException ignore) {
		}

		final Document document  = new Document();
		document.setUserName("testuser1");
		this.permissionDb.ensureWriteAccess(document, new User("testuser1"));
		try {
			this.permissionDb.ensureWriteAccess(document, new User("testuser2"));
			fail("expected exception");
		} catch (ValidationException ignore) {
		}

		this.permissionDb.ensureWriteAccess(new User("testuser1"), "testuser1");
		try {
			this.permissionDb.ensureWriteAccess(new User("testuser1"), "testuser2");
			fail("expected exception");
		} catch (ValidationException ignore) {
		}
	}

	/**
	 * tests isAllowedToAccessPostsDocuments
	 */
	@Ignore
	public void isAllowedToAccessPostsDocuments() {
		// TODO: implement me...
	}

	/**
	 * tests ensureAdminAccess
	 */
	@Test
	public void ensureAdminAccess() {
		User user = new User();
		user.setName("testuser1");
		user.setRole(Role.ADMIN);
		// This method must not throw an exception, because users with role
		// ADMIN should have admin access.
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
			tags.add("tag" + i);
			assertFalse(this.permissionDb.exceedsMaxmimumSize(tags));
		}
		for (int i = 10; i < 42; i++) {
			tags.add("tag" + i);
			assertTrue(this.permissionDb.exceedsMaxmimumSize(tags));
		}
	}

	/**
	 * tests isAllowedToAccessUsersOrGroupDocuments
	 */
	@Ignore
	public void isAllowedToAccessUsersOrGroupDocuments() {
		User loginUser = new User("Johnny_B");
		// user page: own posts -> yes
		assertTrue(this.permissionDb.isAllowedToAccessUsersOrGroupDocuments(loginUser, GroupingEntity.USER, "johnny_b", this.dbSession));
		// user page: posts of other users -> no
		assertFalse(this.permissionDb.isAllowedToAccessUsersOrGroupDocuments(loginUser, GroupingEntity.USER, "Berthold_B", this.dbSession));
		// null user -> no
		assertFalse(this.permissionDb.isAllowedToAccessUsersOrGroupDocuments(loginUser, GroupingEntity.USER, null, this.dbSession));

		// loginUser is member of group KDE, loginUser2 is not
		// (both may see public posts)
		loginUser.addGroup(new Group(GroupID.KDE));
		loginUser.addGroup(new Group(GroupID.PUBLIC));
		User loginUser2 = new User("Peter");
		loginUser2.addGroup(new Group(GroupID.PUBLIC));

		// group members are allowed to see posts -> yes
		assertTrue(this.permissionDb.isAllowedToAccessUsersOrGroupDocuments(loginUser, GroupingEntity.GROUP, "kde", this.dbSession));
		// non-group members are not -> no
		assertFalse(this.permissionDb.isAllowedToAccessUsersOrGroupDocuments(loginUser2, GroupingEntity.GROUP, "kde", this.dbSession));
		// non-existent group -> no
		assertFalse(this.permissionDb.isAllowedToAccessUsersOrGroupDocuments(loginUser, GroupingEntity.GROUP, ParamUtils.NOGROUP_NAME, this.dbSession));

		// dummy tests / null values -> no
		assertFalse(this.permissionDb.isAllowedToAccessUsersOrGroupDocuments(new User(), null, null, this.dbSession));
	}
}