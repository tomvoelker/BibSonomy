package org.bibsonomy.webapp.util.spring.security.exceptions;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Signals that a user that was successfully authenticated using LDAP was not
 * found in the database. 
 * 
 * @author rja
 * @version $Id$
 */
public class OpenIdUsernameNotFoundException extends UsernameNotFoundException {

	/**
	 * Default constructor.
	 * 
	 * @param msg
	 */
	public OpenIdUsernameNotFoundException(String msg) {
		super(msg);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -1083350264240144138L;


}
