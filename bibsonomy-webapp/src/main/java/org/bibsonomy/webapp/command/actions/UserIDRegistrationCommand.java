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
package org.bibsonomy.webapp.command.actions;

import java.io.Serializable;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.BaseCommand;

/**
 * @author Stefan Stützer
 */
public class UserIDRegistrationCommand extends BaseCommand implements Serializable {
	
	/**
	 * serial uid
	 */
	private static final long serialVersionUID = 1371638749968299277L;
	
	/**
	 * Holds the details of the user which wants to register (like name, email, password)
	 */
	private User registerUser;

	/**
	 * Registration step
	 */
	private int step = 1;
	
	private boolean rememberMe;
	
	/**
	 * @return register user
	 */
	public User getRegisterUser() {
		return this.registerUser;
	}
	
	/**
	 * Sets register user 
	 * @param registerUser
	 */
	public void setRegisterUser(User registerUser) {
		this.registerUser = registerUser;
	}
	
	/**
	 * @return registration step
	 */
	public int getStep() {
		return this.step;
	}
	
	/**
	 * Sets registration step
	 * @param step
	 */
	public void setStep(int step) {
		this.step = step;
	}

	/**
	 * @return If the user wants to stay logged in. 
	 */
	public boolean getRememberMe() {
		return this.rememberMe;
	}

	/**
	 * @param rememberMe
	 */
	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}	
}