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
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.webapp.command.HomepageCommand;

/**
 * @author nilsraabe
 */
public class LimitedAccountActivationCommand extends HomepageCommand implements Serializable{
	private static final long serialVersionUID = -4665591098903280881L;
	
	/**
	 * Checkbox for activate SAML Account
	 */
	private boolean checkboxAccept;

	private String submit;
	
	/**
	 * Holds the details of the user which wants to register (like name, email, password)
	 */
	private User registerUser = new User();

	/**
	 * @return the checkboxAccept
	 */
	public boolean isCheckboxAccept() {
		return this.checkboxAccept;
	}

	/**
	 * @param checkboxAccept the checkboxAccept to set
	 */
	public void setCheckboxAccept(boolean checkboxAccept) {
		this.checkboxAccept = checkboxAccept;
	}

	/**
	 * @return the submit
	 */
	public String getSubmit() {
		return this.submit;
	}
	
	/**
	 * @return whether the submit button was clicked
	 */
	public boolean isSubmitted() {
		return ValidationUtils.present(this.submit);
	}

	/**
	 * @param submit the submit to set
	 */
	public void setSubmit(String submit) {
		this.submit = submit;
	}

	/**
	 * @return the registerUser
	 */
	public User getRegisterUser() {
		return this.registerUser;
	}

	/**
	 * @param registerUser the registerUser to set
	 */
	public void setRegisterUser(User registerUser) {
		this.registerUser = registerUser;
	}
}
