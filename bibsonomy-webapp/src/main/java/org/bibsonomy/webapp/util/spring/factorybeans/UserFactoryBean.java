/*
 * Created on 19.08.2007
 */
package org.bibsonomy.webapp.util.spring.factorybeans;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.model.User;
import org.springframework.beans.factory.FactoryBean;

import beans.UserBean;
import filters.InitUserFilter;

/**
 * fishes the {@link User} out of the request
 *  
 * @see FactoryBean
 * @author Dominik Benz
 */
public class UserFactoryBean implements FactoryBean {
	private HttpServletRequest request;
	private User instance;
	
	/**
	 * @param request
	 */
	public void setRequest(final HttpServletRequest request) {
		this.request = request;
	}

	public Object getObject() throws Exception {
		if (instance == null) {
			instance = getLoginUser(request);
		}
		return instance;
	}

	/**
	 * @param req
	 * @return the {@link User} model of the logged in user 
	 */
	protected User getLoginUser(@SuppressWarnings("unused") final HttpServletRequest req) {
		// FIXME: IoC break: use user object instead of accessing request
		// FIXME: use bibsonomy2 user object and check password again
		return (User) this.request.getAttribute(InitUserFilter.REQ_ATTRIB_LOGIN_USER);
	}

	public Class<?> getObjectType() {
		return User.class;
	}

	public boolean isSingleton() {
		return false;  // TODO: check if singleton is really only singleton in the scope of the factorybean 
	}

}
