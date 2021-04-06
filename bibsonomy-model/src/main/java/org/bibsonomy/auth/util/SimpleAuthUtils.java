package org.bibsonomy.auth.util;

import org.bibsonomy.common.enums.Role;
import org.bibsonomy.model.User;

/**
 * simple auth utils
 *
 * @author dzo
 */
public final class SimpleAuthUtils {
	private SimpleAuthUtils() {
		// noop
	}

	/**
	 * returns true if the provided user has at least the provided user role
	 * @param user
	 * @param role
	 * @return
	 */
	public static boolean hasAtLeastUserRole(final User user, final Role role) {
		// first case the user has the provided role
		final Role userRole = user.getRole();
		if (userRole.equals(role)) {
			return true;
		}

		// else the role is implied from the user role
		return Role.getImpliedRoles(userRole).contains(role);
	}
}
