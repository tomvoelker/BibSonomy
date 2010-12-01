package org.bibsonomy.webapp.util.spring.security.exceptionmapper;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.util.spring.security.exceptions.LdapUsernameNotFoundException;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.LdapUtils;

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
			 * copy the password (code copied from spring)
			 */
			final Object passo = ctx.getObjectAttribute("userPassword");
			if (present(passo)) {
				final String password = LdapUtils.convertPasswordToString(passo);
				user.setPassword(password);
			}

		}

		return user;
	}

}
