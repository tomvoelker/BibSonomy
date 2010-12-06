package org.bibsonomy.webapp.util.spring.security.exceptionmapper;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.util.spring.security.exceptions.OpenIdUsernameNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author rja
 * @version $Id$
 */
public class OpenIdUsernameNotFoundExceptionMapper extends UsernameNotFoundExceptionMapper {

	/**
	 * Checks if this mapper can handle the given exception.
	 * 
	 * @param e
	 * @return <code>true</code> if the given exception is a subclass of {@link OpenIdUsernameNotFoundException}.
	 */
	@Override
	public boolean supports(final UsernameNotFoundException e) {
		return present(e) && OpenIdUsernameNotFoundException.class.isAssignableFrom(e.getClass());
	}
	
	/**
	 * Maps the user data from the OpenID server to our user object.
	 * 
	 * @param e
	 * @return A user containing the information from the OpenID server.
	 */
	@Override
	public User mapToUser(final UsernameNotFoundException e) {
		final User user = new User();
		if (e instanceof OpenIdUsernameNotFoundException) {
			final Authentication authentication = ((OpenIdUsernameNotFoundException) e).getAuthentication();
			/*
			 * TODO: fill user
			 */
		}
		
		return user;
	}
	
}
