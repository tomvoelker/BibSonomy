package org.bibsonomy.webapp.config;

/**
 * Identifier for all supported authentication methods.
 * 
 * @author folke
 * @version $Id$
 */
public enum AuthMethods {
	DB("DB"),
	LDAP("LDAP"),
	OPENID("OpenId"),
	X509("X.509");
	
	String id;
	AuthMethods(String id) {
		this.id = id;
	}
	
    /**
     * Retrieve Method by name
     * 
     * @param name
     *            the requested authentication method (e.g. "OpenId")
     * @return the corresponding Order enum
     */
	public static AuthMethods getAuthMethodByName(String name) throws IllegalArgumentException {
		try {
			return AuthMethods.valueOf(name.toUpperCase());
		} catch (NullPointerException np) {
			throw new IllegalArgumentException("No authentication method!");
		} catch (IllegalArgumentException ia) {
			throw new IllegalArgumentException("Requested order not supported. Possible values are 'DB', 'LDAP', 'OpenId', 'X.509'");
		}
	}

}
