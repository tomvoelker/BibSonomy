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
	 * check whether given authentication method is enabled
	 * @param methodName name of the authentication method in question
	 * @return true if given authentication method is configured
	 */
	public boolean containsAuthMethod(String methodName) {
		AuthMethod authMethod = AuthMethod.getAuthMethodByName(methodName);
		return this.containsAuthMethod(authMethod);
	}
	
	/**
	 * check whether given authentication method is enabled
	 * @param method the authentication method in question
	 * @return true if given authentication method is configured
	 */
	public boolean containsAuthMethod(AuthMethod method) {
		return this.authOrder.contains(method);
	}	
	
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
