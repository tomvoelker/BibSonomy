package org.bibsonomy.webapp.util.spring.security.exceptions;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Signals that a user that was successfully authenticated using LDAP was not
 * found in the database. 
 * 
 * @author rja
 * @version $Id$
 */
public class LdapUsernameNotFoundException extends UsernameNotFoundException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1083350264240144138L;
	private final DirContextOperations dirContextOperations;
	
	/**
	 * @param msg
	 * @param dirContextOperations
	 */
	public LdapUsernameNotFoundException(String msg, DirContextOperations dirContextOperations) {
		super(msg);
		this.dirContextOperations = dirContextOperations;
	}

	/**
	 * @return The context from LDAP.
	 */
	public DirContextOperations getDirContextOperations() {
		return this.dirContextOperations;
	}

}
