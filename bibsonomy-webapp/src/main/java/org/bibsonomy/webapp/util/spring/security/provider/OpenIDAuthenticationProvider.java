package org.bibsonomy.webapp.util.spring.security.provider;

import org.bibsonomy.webapp.util.spring.security.exceptions.OpenIdUsernameNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * When a user is successfully authenticated using OpenID but we can't find him
 * in our database, we want to send him to a registration form where the fields
 * are filled with his OpenID data.
 * 
 * The only place to add this information is here, because where we find out
 * that the user is not registered, yet, we don't have the OpenID authentication
 * to put it into the exception.
 * 
 * @author rja
 * @version $Id$
 */
public class OpenIDAuthenticationProvider extends org.springframework.security.openid.OpenIDAuthenticationProvider {

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		try {
			return super.authenticate(authentication);
		} catch (final OpenIdUsernameNotFoundException e) {
			/*
			 * Add user data to exception and re-throw it. The data is later 
			 * stored in the session and used for filling the registration form. 
			 */
			e.setAuthentication(authentication);
			throw e;
		}
	}
}
