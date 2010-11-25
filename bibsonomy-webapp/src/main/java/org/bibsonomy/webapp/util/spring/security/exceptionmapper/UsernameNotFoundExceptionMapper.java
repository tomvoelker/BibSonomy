package org.bibsonomy.webapp.util.spring.security.exceptionmapper;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.util.spring.security.exceptions.LdapUsernameNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Maps the data from {@link UsernameNotFoundException} subclass onto our 
 * user object.
 * 
 * @author rja
 * @version $Id$
 */
public abstract class UsernameNotFoundExceptionMapper {

	private String redirectUrl;
	
	/**
	 * Checks if this mapper can handle the given exception.
	 * 
	 * @param e
	 * @return <code>true</code> if the given exception is a subclass of {@link LdapUsernameNotFoundException}.
	 */
	public abstract boolean supports(final UsernameNotFoundException e);

	/**
	 * Maps the user data from the LDAP/OpenID/whatever server to our user object.
	 * 
	 * @param e
	 * @return A user containing the information from the LDAP server.
	 */
	public abstract User mapToUser(final UsernameNotFoundException e);

	/**
	 * @return The URL to which the user shall be redirected if she/he needs to
	 * register first.
	 */
	public String getRedirectUrl() {
		return this.redirectUrl;
	}

	/**
	 * The URL to which the user shall be redirected if she/he needs to
	 * register first.
	 * 
	 * @param redirectUrl
	 */
	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	
}