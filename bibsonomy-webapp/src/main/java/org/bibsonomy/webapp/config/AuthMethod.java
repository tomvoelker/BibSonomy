package org.bibsonomy.webapp.config;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.util.StringUtils;

/**
 * Identifier for all supported authentication methods.
 * 
 * @author folke
 * @version $Id$
 */
public enum AuthMethod {
	
	/**
	 * TODO
	 */
	INTERNAL,
	
	/**
	 * TODO
	 */
	LDAP,
	
	/**
	 * TODO
	 */
	OPENID;
	
	/**
	 * TODO implement x509
	 */
//	X509;
	
    /**
     * Retrieve Method by name
     * 
     * @param name
     *            the requested authentication method (e.g. "OpenId")
     * @return the corresponding Order enum
     * @throws IllegalArgumentException 
     */
	public static AuthMethod getAuthMethodByName(String name) throws IllegalArgumentException {
		if (!present(name)) {
			throw new IllegalArgumentException("No authentication method!");
		}
		try {
			return AuthMethod.valueOf(name.toUpperCase());
		} catch (final IllegalArgumentException ia) {
			throw new IllegalArgumentException("Requested order not supported. Possible values are " + StringUtils.implodeStringArray(AuthMethod.values(), ", "));
		}
	}

}
