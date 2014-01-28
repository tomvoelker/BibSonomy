package org.bibsonomy.webapp.util.spring.security.exceptions;

import org.bibsonomy.common.enums.AuthMethod;


/**
 * Signals that an authentication attempt of a disabled authentication method was detected
 * @author jensi
 */
public class UnsupportedAuthenticationMethodException extends RuntimeException {
	private static final long serialVersionUID = 1459344776558363035L;
	private final AuthMethod authMethod;

	/**
	 * @param authMethod
	 */
	public UnsupportedAuthenticationMethodException(AuthMethod authMethod) {
		super("" + authMethod);
		this.authMethod = authMethod;
	}

	/**
	 * @return the authMethod
	 */
	public AuthMethod getAuthMethod() {
		return this.authMethod;
	}
}
