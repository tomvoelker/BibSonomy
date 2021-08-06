/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
public class UserSettingsFactoryBean implements FactoryBean<UserSettings> {
	private UserSettings instance;
	private User user;
	
	@Override
	public UserSettings getObject() throws Exception {
		if (instance == null) {
			if (this.user.getSettings() == null) {
				instance = new UserSettings();
			} else {
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
