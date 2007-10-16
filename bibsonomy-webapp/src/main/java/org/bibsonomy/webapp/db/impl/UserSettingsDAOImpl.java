/*
 * Created on 19.08.2007
 */
package org.bibsonomy.webapp.db.impl;

import javax.servlet.ServletRequest;

import org.bibsonomy.model.UserSettings;
import org.bibsonomy.webapp.db.UserSettingsDAO;

public class UserSettingsDAOImpl implements UserSettingsDAO {
	private final ServletRequest request;

	public UserSettingsDAOImpl(ServletRequest request) {
		this.request = request;
	}

	public UserSettings getUserSettings() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setUserSettings(UserSettings settings) {
		// TODO Auto-generated method stub
		
	}
}
