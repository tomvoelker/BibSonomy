package com.atlassian.confluence.user;

import com.atlassian.user.User;

/**
 * Stores a user object for different threads.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class AuthenticatedUserThreadLocal {

	private static final ThreadLocal<User> localUser = new ThreadLocal<User>();

	/**
	 * @param user
	 */
	public static void setUser(final User user) {
		localUser.set(user);
	}

	/**
	 * @return user
	 */
	public static User getUser() {
		return localUser.get();
	}
}