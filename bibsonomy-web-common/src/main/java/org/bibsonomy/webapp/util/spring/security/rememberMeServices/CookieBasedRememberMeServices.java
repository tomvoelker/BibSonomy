package org.bibsonomy.webapp.util.spring.security.rememberMeServices;

import org.springframework.security.web.authentication.RememberMeServices;

/**
 * @author dzo
 * @version $Id$
 */
public interface CookieBasedRememberMeServices extends RememberMeServices {
	
	/**
	 * @return the name of the cookie
	 */
	public String getCookieName();
}
