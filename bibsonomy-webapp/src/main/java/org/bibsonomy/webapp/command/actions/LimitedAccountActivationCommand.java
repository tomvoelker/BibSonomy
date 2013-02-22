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
 * @version $Id$
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
