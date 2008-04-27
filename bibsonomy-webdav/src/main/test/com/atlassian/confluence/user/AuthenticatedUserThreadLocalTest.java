package com.atlassian.confluence.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.user.User;

/**
 * Checks whether AuthenticatedUserThreadLocal maintains different instances of
 * User objects for different threads.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class AuthenticatedUserThreadLocalTest {

	private static User user1;
	private static User user2;

	/**
	 * setup
	 */
	@Before
	public void setup() {
		user1 = null;
		user2 = null;
		AuthenticatedUserThreadLocal.setUser(null);
	}

	/**
	 * @throws InterruptedException
	 */
	@Test
	public void test() throws InterruptedException {
		assertNull(user1);
		assertNull(user2);
		assertNull(AuthenticatedUserThreadLocal.getUser());

		new Thread(getRunnable(1)).start();
		new Thread(getRunnable(2)).start();
		Thread.sleep(100);

		assertNull(AuthenticatedUserThreadLocal.getUser());
		assertNotSame(user1, user2);
		assertEquals("Testuser1", user1.getFullName());
		assertEquals("Testuser2", user2.getFullName());
	}

	private Runnable getRunnable(final int globalUser) {
		return new Runnable() {
			public void run() {
				final User user = new User() {
					public String getEmail() {
						return null;
					}

					public String getFullName() {
						return "Testuser" + globalUser;
					}

					public void setEmail(String email) {
					}

					public void setFullName(String fullName) {
					}

					public void setPassword(String password) {
					}
				};
				assertNull(AuthenticatedUserThreadLocal.getUser());
				AuthenticatedUserThreadLocal.setUser(user);
				assertNotNull(AuthenticatedUserThreadLocal.getUser());

				if (globalUser == 1) {
					user1 = AuthenticatedUserThreadLocal.getUser();
				} else {
					user2 = AuthenticatedUserThreadLocal.getUser();
				}
			}
		};
	}
}