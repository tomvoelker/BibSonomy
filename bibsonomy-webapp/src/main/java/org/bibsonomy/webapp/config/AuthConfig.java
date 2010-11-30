package org.bibsonomy.webapp.config;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;

/**
 * 
 * 
 * @author folke
 * @version $Id$
 */
public class AuthConfig implements InitializingBean {
	
	private List<AuthMethod> authOrder;
	private String[] authOrderString;
	
	/**
	 * @return the authOrder
	 */
	public List<AuthMethod> getAuthOrder() {
		return this.authOrder;
	}

	/**
	 * @param authOrderString the authOrderString to set
	 */
	public void setAuthOrderString(String[] authOrderString) {
		this.authOrderString = authOrderString;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (!present(this.authOrderString)) throw new IllegalArgumentException();
		
		// parse auth methods
		this.authOrder = new LinkedList<AuthMethod>();
		for (final String authMethodString : this.authOrderString) {
			this.authOrder.add(AuthMethod.getAuthMethodByName(authMethodString));
		}
	}
}
