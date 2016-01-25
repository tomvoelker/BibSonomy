/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
import java.util.List;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.webapp.command.SimpleResourceViewCommand;

/**
 * @author nilsraabe
 */
public class LimitedAccountActivationCommand  extends SimpleResourceViewCommand implements Serializable{
	private static final long serialVersionUID = -4665591098903280881L;

	/**
	 * Fills the news box in the sidebar
	 */
	private List<Post<Bookmark>> news;
	
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
	 * @return The latest news posts.
	 */
	public List<Post<Bookmark>> getNews() {
		return this.news;
	}

	/**
	 * @param news 
	 */
	public void setNews(List<Post<Bookmark>> news) {
		this.news = news;
	}

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
