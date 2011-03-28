package org.bibsonomy.webapp.util.spring.security.exceptionmapper;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.util.spring.security.exceptions.LdapUsernameNotFoundException;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author rja
 * @version $Id$
 */
public class LdapUsernameNotFoundExceptionMapper extends UsernameNotFoundExceptionMapper {

	@Override
	public boolean supports(final UsernameNotFoundException e) {
		return present(e) && LdapUsernameNotFoundException.class.isAssignableFrom(e.getClass());
	}

	@Override
	public User mapToUser(final UsernameNotFoundException e) {
		final User user = new User();
		if (e instanceof LdapUsernameNotFoundException) {
			final DirContextOperations ctx = ((LdapUsernameNotFoundException) e).getDirContextOperations();

			/*
			 * copy user attributes
			 */
			user.setRealname(ctx.getStringAttribute("givenname") + " " + ctx.getStringAttribute("sn"));
			user.setEmail(ctx.getStringAttribute("mail"));
			user.getSettings().setDefaultLanguage(ctx.getStringAttribute("preferredlanguage"));
			user.setPlace(ctx.getStringAttribute("l")); // location
			user.setLdapId(ctx.getStringAttribute("uid"));

			/*
			 * After successful registration the user is logged in using the
			 * plain text password. Thus, we store it here in the user. 
			 */
			final Object credentials = e.getAuthentication().getCredentials();
			if (credentials instanceof String) {
				user.setPassword((String) credentials);
			}
		}

		return user;
	}

}
