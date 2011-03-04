package org.bibsonomy.util.spring.security;

import org.bibsonomy.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * class for accessing credentials as provided by spring security
 * 
 * @author fei, dzo
 */
public class AuthenticationUtils {

	/**
	 * Small helper method for Servlets to easily retrieve User.
	 * 
	 * FIXME: How does this work? Using a static method to retrieve thread-specific
	 * information? Looks like some Java magic. :-O - yes it is. the context is saved in a ThreadLocal
	 * 
	 * @return the user
	 */
	@Deprecated
	public static User getUser() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			final Object principal = authentication.getPrincipal();
			if (principal != null && principal instanceof UserAdapter) {
				final UserAdapter adapter = (UserAdapter) principal;
				return adapter.getUser();
			}
		}
		
		return new User();
	}

}
