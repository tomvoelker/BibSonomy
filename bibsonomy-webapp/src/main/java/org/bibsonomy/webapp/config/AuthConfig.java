package org.bibsonomy.webapp.config;

import org.apache.commons.lang.StringUtils;

/**
 * Bean for managing runtime configuration of the authorization process.
 * 
 * @author folke
 * @version $Id$
 */
public class AuthConfig {
	/** delimiter for concatenated lists */
	public final static String DELIMITER = ",";
	
	/** 
	 * configures which authentication methods are available and determines 
	 * their presentation order
	 * @see{#AuthMethods} 
	 */
	private String[] authOrder;
	

	public void setAuthOrderString(String authOrder) {
		this.setAuthOrder(authOrder.split(DELIMITER));
	}

	public String getAuthOrderString() {
		return StringUtils.join(getAuthOrder(), DELIMITER);
	}

	public void setAuthOrder(String[] authOrder) throws IllegalArgumentException {
		// parse input strings to ensure that only supported methods are supported
		this.authOrder = new String[authOrder.length];
		for( int i=0; i<authOrder.length; i++ ) {
			this.authOrder[i] = AuthMethods.getAuthMethodByName(authOrder[i]).name();
		}
	}

	public String[] getAuthOrder() {
		return authOrder;
	}
}
