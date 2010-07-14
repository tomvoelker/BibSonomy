/*
 * Created on 19.08.2007
 */
package org.bibsonomy.webapp.util.spring.factorybeans;

import org.bibsonomy.model.User;
import org.bibsonomy.model.UserSettings;
import org.springframework.beans.factory.FactoryBean;

/**
 * accesses the user settings of the logged in user
 *  
 * @see FactoryBean
 * @author Jens Illig
 */
public class UserSettingsFactoryBean implements FactoryBean {
	private UserSettings instance;
	private User user;
	
	@Override
	public Object getObject() throws Exception {
		if (instance == null) {
			if (this.user.getSettings() == null) {
				instance = new UserSettings();
			}
			else {
				instance = this.user.getSettings();
			}
		}
		return instance;
	}

	@Override
	public Class<?> getObjectType() {
		return UserSettings.class;
	}

	/**
	 * @param user
	 */
	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public boolean isSingleton() {
		return false;  // TODO: check if singleton is really only singleton in the scope of the factorybean 
	}

}
