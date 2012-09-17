package org.bibsonomy.webapp.util.spring.security.rememberMeServices;

import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Adapter to make the {@link #getCookieName()} of {@link org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices}
 * public
 * 
 * @author dzo
 * @version $Id$
 */
public class TokenBasedRememberMeServices extends org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices implements CookieBasedRememberMeServices {
	
	/**
	 * default constructor
	 * 
	 * @param key
	 * @param userDetailsService
	 */
	public TokenBasedRememberMeServices(final String key, final UserDetailsService userDetailsService) {
		super(key, userDetailsService);
	}

	@Override
	public String getCookieName() {
		return super.getCookieName();
	}
}
