/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.database.managers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupLevelPermission;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.GroupMembership;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.testutil.ParamUtils;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Robert Jäschke
 */
public class PermissionDatabaseManagerTest extends AbstractDatabaseManagerTest {
	
	private static PermissionDatabaseManager permissionDb;
	
	/**
	 * sets up the permission manager
	 */
	@BeforeClass
	public static void setupManager() {
		permissionDb = PermissionDatabaseManager.getInstance();
	}

	/**
	 * tests checkStartEnd
	 */
	@Test
	public void checkStartEnd() {
		// ok
		for (int i = 0; i <= 1000; i++) {
			try {
				permissionDb.checkStartEnd(new User(), 0, i, "test");
			} catch (final AccessDeniedException ignore) {
				fail("no exception expected");
			}
		}
		// not ok
		for (int i = 1001; i < 10000; i++) {
			try {
				permissionDb.checkStartEnd(new User(), 0, i, "test");
				fail("expected exception");
			} catch (final AccessDeniedException ignore) {
			}
		}
		// OK 
		final User admin = new User();
		admin.setRole(Role.ADMIN);
		for (int i = 1001; i < 10000; i++) {
			try {
				permissionDb.checkStartEnd(admin, 0, i, "test");
			} catch (final AccessDeniedException ignore) {
				fail("no exception expected");
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
		permissionDb.ensureWriteAccess(post, new User("testuser1"));
		try {
			permissionDb.ensureWriteAccess(post, new User("testuser2"));
			fail("expected exception");
		} catch (final AccessDeniedException ignore) {
			// ignore
		}

		final Document document  = new Document();
		document.setUserName("testuser1");
		permissionDb.ensureWriteAccess(new User("testuser1"), document.getUserName());
		try {
			permissionDb.ensureWriteAccess(new User("testuser2"), document.getUserName());
			fail("expected exception");
		} catch (final AccessDeniedException ignore) {
			// ignore
		}

		permissionDb.ensureWriteAccess(new User("testuser1"), "testuser1");
		try {
			permissionDb.ensureWriteAccess(new User("testuser1"), "testuser2");
			fail("expected exception");
		} catch (final AccessDeniedException ignore) {
			// ignore
		}
	}

	/**
	 * tests isAllowedToAccessPostsDocuments
	 */
	@Ignore
	@Test
	public void isAllowedToAccessPostsDocuments() {
		// TODO: implement test
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
		permissionDb.ensureAdminAccess(user);

		for (final String name : new String[] { "", " ", null }) {
			user = new User();
			user.setName(name);

			// This must throw an exception, because users with role DEFAULT
			// must not have admin access.
			try {
				user.setRole(Role.DEFAULT);
				permissionDb.ensureAdminAccess(user);
				fail("should throw an exception");
			} catch (final AccessDeniedException ignore) {
				// ignore
			}

			// This must throw an exception, because users without a name
			// (independent of their role) must not have admin access.
			try {
				user.setRole(Role.ADMIN);
				permissionDb.ensureAdminAccess(user);
				fail("should throw an exception");
			} catch (final AccessDeniedException ignore) {
				// ignore
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
			assertFalse(permissionDb.useResourceSearchForTagQuery(tags.size()));
		}
		for (int i = 10; i < 42; i++) {
			tags.add("tag" + i);
			assertTrue(permissionDb.useResourceSearchForTagQuery(tags.size()));
		}
	}

	/**
	 * tests isAllowedToAccessUsersOrGroupDocuments
	 */
	@Test
	public void testIsAllowedToAccessUsersOrGroupDocuments() {
		final User loginUser = new User("testuser1");
		// user page: own posts -> yes
		assertTrue(permissionDb.isAllowedToAccessUsersOrGroupDocuments(loginUser, GroupingEntity.USER, "testuser1", null, this.dbSession));
		// user page: posts of other users -> no
		assertFalse(permissionDb.isAllowedToAccessUsersOrGroupDocuments(loginUser, GroupingEntity.USER, "testuser2", null, this.dbSession));
		// null user -> no
		assertFalse(permissionDb.isAllowedToAccessUsersOrGroupDocuments(loginUser, GroupingEntity.USER, null, null, this.dbSession));
		// user not logged in -> no
		assertFalse(permissionDb.isAllowedToAccessUsersOrGroupDocuments(new User(), GroupingEntity.USER, "testuser1", null, this.dbSession));

		// loginUser is member of group testgroup1, loginUser2 is not
		// (both may see public posts)
		// FIXME: Manually add the correct groups and userSharedDocuments settings
		loginUser.addGroup(GroupUtils.getPublicGroup());
		
		final User loginUser2 = new User("testuser1");
		loginUser2.addGroup(GroupUtils.getPublicGroup());
		
		final Group testgroup1 = new Group(TESTGROUP1_ID);
		final GroupMembership groupMembership1 = new GroupMembership();
		groupMembership1.setUser(loginUser);
		groupMembership1.setUserSharedDocuments(true);
		testgroup1.getMemberships().add(groupMembership1);
		testgroup1.setSharedDocuments(true);
		loginUser.addGroup(testgroup1);

		final Group testgroup2 = new Group(TESTGROUP2_ID);
		final GroupMembership groupMembership2 = new GroupMembership();
		groupMembership2.setUser(loginUser);
		groupMembership2.setUserSharedDocuments(true);
		testgroup2.getMemberships().add(groupMembership2);
		testgroup2.setSharedDocuments(false);
		loginUser.addGroup(testgroup2);

		final Group testgroup3 = new Group(TESTGROUP3_ID);
		final GroupMembership groupMembership3 = new GroupMembership();
		groupMembership3.setUser(loginUser);
		groupMembership3.setUserSharedDocuments(false);
		testgroup3.getMemberships().add(groupMembership3);
		testgroup3.setSharedDocuments(false);
		loginUser.addGroup(testgroup3);

		final Group testgroup4 = new Group(TESTGROUP4_ID);
		final GroupMembership groupMembership4 = new GroupMembership();
		groupMembership4.setUser(loginUser);
		groupMembership4.setUserSharedDocuments(false);
		testgroup4.getMemberships().add(groupMembership4);
		testgroup4.setSharedDocuments(true);
		loginUser.addGroup(testgroup4);
		
		// non-existent group -> no
		assertFalse(permissionDb.isAllowedToAccessUsersOrGroupDocuments(loginUser, GroupingEntity.GROUP, ParamUtils.NOGROUP_NAME, null, this.dbSession));
	
		// non-group members are not -> no
		assertFalse(permissionDb.isAllowedToAccessUsersOrGroupDocuments(loginUser2, GroupingEntity.GROUP, "testgroup1", null, this.dbSession));
		
		// dummy tests / null values -> no
		assertFalse(permissionDb.isAllowedToAccessUsersOrGroupDocuments(new User(), null, null, null, this.dbSession));
		
		// group sharedDocuments = 0 && userSharedDocuments = 0 -> no
		assertFalse(permissionDb.isAllowedToAccessUsersOrGroupDocuments(loginUser, GroupingEntity.GROUP, "testgroup3", null, this.dbSession));
		
		// group sharedDocuments = 0 && userSharedDocuments = 1 -> no
		assertFalse(permissionDb.isAllowedToAccessUsersOrGroupDocuments(loginUser, GroupingEntity.GROUP, "testgroup2", null, this.dbSession));
		
		// group sharedDocuments = 1 && userSharedDocuments = 0 -> yes (because we have group setting)
		assertTrue(permissionDb.isAllowedToAccessUsersOrGroupDocuments(loginUser, GroupingEntity.GROUP, "testgroup4", null, this.dbSession));
		
		// group sharedDocuments = 1 && userSharedDocuments = 1 -> yes
		assertTrue(permissionDb.isAllowedToAccessUsersOrGroupDocuments(loginUser, GroupingEntity.GROUP, "testgroup1", null, this.dbSession));
	}
	
	@Test
	public void testHasGroupLevelPermission() {
		assertTrue(permissionDb.hasGroupLevelPermission(new User("testuser1"), GroupLevelPermission.COMMUNITY_POST_INSPECTION, this.dbSession));
		assertTrue(permissionDb.hasGroupLevelPermission(new User("testuser2"), GroupLevelPermission.COMMUNITY_POST_INSPECTION, this.dbSession));
		assertFalse(permissionDb.hasGroupLevelPermission(new User("testuser3"), GroupLevelPermission.COMMUNITY_POST_INSPECTION, this.dbSession));
	}
}