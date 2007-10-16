/*
 * Created on 19.08.2007
 */
package org.bibsonomy.webapp.util.spring.factorybeans;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.database.DBLogicInterfaceFactory;
import org.bibsonomy.model.UserSettings;
import org.bibsonomy.model.logic.PostLogicInterface;
import org.springframework.beans.factory.FactoryBean;

import beans.UserBean;
import filters.InitUserFilter;

public class UserSettingsFactoryBean implements FactoryBean {
	private HttpServletRequest request;
	private UserSettings instance;
	
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

	protected UserBean getLoginUser(final HttpServletRequest req) {
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
