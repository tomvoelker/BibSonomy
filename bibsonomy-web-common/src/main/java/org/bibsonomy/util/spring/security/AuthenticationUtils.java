package org.bibsonomy.util.spring.security;

import org.bibsonomy.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * class for accessing credentials as provided by spring security
 * 
 * @author fei, dzo
 * @version $Id$
 */
public class AuthenticationUtils {

	/**
	 * Small helper method to easily retrieve the logged in user. If nobody is logged in, an anonymous dummy user is created.
	 * 
	 * How does this work? Using a static method to retrieve thread-specific
	 * information? Looks like some Java magic. :-O - yes it is. the context is
	 * saved in a ThreadLocal
	 * 
	 * @return the user (never null)
	 */
	public static User getUser() {
		User user = getUserOrNull();
		if (user != null) {
			return user;
		}
		return new User();
	}

	/**
	 * 	Small helper method to easily retrieve the logged in user. In contrast to {@link #getUser()}, this may return null.
	 * @return the user or null
	 */
	public static User getUserOrNull() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			final Object principal = authentication.getPrincipal();
			if (principal != null && principal instanceof UserAdapter) {
				final UserAdapter adapter = (UserAdapter) principal;
				return adapter.getUser();
			}
		}
		return null;
	}

}
