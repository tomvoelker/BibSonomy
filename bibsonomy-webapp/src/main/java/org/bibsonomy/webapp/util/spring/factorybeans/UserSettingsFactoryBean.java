/*
 * Created on 19.08.2007
 */
package org.bibsonomy.webapp.util.spring.factorybeans;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.model.UserSettings;
import org.springframework.beans.factory.FactoryBean;

import beans.UserBean;
import filters.InitUserFilter;

/**
 * fishes the {@link UserSettings} out of the request
 *  
 * @see FactoryBean
 * @author Jens Illig
 */
public class UserSettingsFactoryBean implements FactoryBean {
	private HttpServletRequest request;
	private UserSettings instance;
	
	/**
	 * @param request
	 */
	public void setRequest(final HttpServletRequest request) {
		this.request = request;
	}

	public Object getObject() throws Exception {
		if (instance == null) {
			final UserBean userKeyData = getLoginUser(request);
			instance = new UserSettings();
			instance.setItemsPerPage(userKeyData.getItemcount());
		}
		return instance;
	}

	/**
	 * @param req
	 * @return currently the old {@link UserBean} of the logged in user
	 */
	protected UserBean getLoginUser(@SuppressWarnings("unused") final HttpServletRequest req) {
		// FIXME: IoC break: use user object instead of accessing request
		// FIXME: use bibsonomy2 user object and check password again
		return (UserBean) this.request.getAttribute(InitUserFilter.REQ_ATTRIB_USER); 
	}

	public Class<?> getObjectType() {
		return UserSettings.class;
	}

	public boolean isSingleton() {
		return false;  // TODO: check if singleton is really only singleton in the scope of the factorybean 
	}

}
