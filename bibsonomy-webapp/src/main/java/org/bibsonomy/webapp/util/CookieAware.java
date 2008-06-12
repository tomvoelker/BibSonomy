package org.bibsonomy.webapp.util;


/**
 * Interface to allow controllers access to the cookie logic. 
 * 
 * @author rja
 * @version $Id$
 */
public interface CookieAware {

	
	/** 
	 * @param cookieLogic
	 */
	public void setCookieLogic(CookieLogic cookieLogic);
}
