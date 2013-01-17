package org.bibsonomy.webapp.util.spring.security.exceptions;

import org.bibsonomy.common.enums.AuthMethod;
import org.springframework.security.core.AuthenticationException;

/**
 * Signals that a login via a special method should be performed.  
 * 
 * @author jil
 * @version $Id$
 */
public class SpecialAuthMethodRequiredException extends AuthenticationException {

	private final AuthMethod requiredAuthMethod;

	/**
	 * @param requiredAuthMethod
	 */
	public SpecialAuthMethodRequiredException(AuthMethod requiredAuthMethod) {
		super(requiredAuthMethod.name());
		this.requiredAuthMethod = requiredAuthMethod;
	}

	private static final long serialVersionUID = -4024089851754389755L;

	public AuthMethod getRequiredAuthMethod() {
		return this.requiredAuthMethod;
	}
	
}
