/*
 * Created on 19.08.2007
 */
package org.bibsonomy.webapp.db;

import org.bibsonomy.model.UserSettings;

public interface UserSettingsDAO {
	public UserSettings getUserSettings();
	public void setUserSettings(UserSettings settings);
}
