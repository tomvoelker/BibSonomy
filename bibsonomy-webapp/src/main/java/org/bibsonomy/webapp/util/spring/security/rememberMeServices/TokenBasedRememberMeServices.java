package org.bibsonomy.webapp.util.spring.security.rememberMeServices;

/**
 * Adapter to make the {@link #getCookieName()} of {@link org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices}
 * public
 * 
 * @author dzo
 * @version $Id$
 */
public class TokenBasedRememberMeServices extends org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices implements CookieBasedRememberMeServices {
	
	@Override
	public String getCookieName() {
		return super.getCookieName();
	}
}
