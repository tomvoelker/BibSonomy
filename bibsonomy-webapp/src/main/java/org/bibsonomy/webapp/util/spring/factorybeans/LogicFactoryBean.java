/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
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

import org.bibsonomy.database.DBLogicUserInterfaceFactory;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.util.ReadOnlyLogic;
import org.springframework.beans.factory.FactoryBean;

/**
 * lets {@link DBLogicUserInterfaceFactory} appear as a FactoryBean, which itself
 * is customizable in the spring context.
 *  
 * @see FactoryBean
 * @author Jens Illig
 */
public abstract class LogicFactoryBean extends DBLogicUserInterfaceFactory implements FactoryBean<LogicInterface> {

	private User user;
	private LogicInterface instance;
	private boolean readOnly;
	
	@Override
	public LogicInterface getObject() throws Exception {
		if (instance == null) {
			instance = ReadOnlyLogic.maskLogic(this.getLogicAccess(user.getName(), ""), this.readOnly);
		}
		return instance;
	}
	
	@Override
	protected User getLoggedInUser(String loginName, String password) {
		// can always return true because user object attribute in request is always valid;
		return user;
	}

	@Override
	public Class<?> getObjectType() {
		return LogicInterface.class;
	}

	@Override
	public boolean isSingleton() {
		return false;  // TODO: check if singleton is really only singleton in the scope of the factorybean 
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return this.user;
	}

	/**
	 * @param user the user
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @param readOnly the readOnly to set
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

}
