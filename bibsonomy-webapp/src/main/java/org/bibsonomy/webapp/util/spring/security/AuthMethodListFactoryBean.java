package org.bibsonomy.webapp.util.spring.security;

import java.util.List;

import org.bibsonomy.common.enums.AuthMethod;
import org.springframework.beans.factory.FactoryBean;

/**
 * TODO: better way to get a list of {@link AuthMethod}s
 * 
 * @author dzo
 */
public class AuthMethodListFactoryBean implements FactoryBean<List<AuthMethod>> {

	private List<AuthMethod> authConfig;

	@Override
	public List<AuthMethod> getObject() throws Exception {
		return this.authConfig;
	}

	@Override
	public Class<?> getObjectType() {
		return List.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	/**
	 * @param authConfig the authConfig to set
	 */
	public void setAuthConfig(final List<AuthMethod> authConfig) {
		this.authConfig = authConfig;
	}
}
