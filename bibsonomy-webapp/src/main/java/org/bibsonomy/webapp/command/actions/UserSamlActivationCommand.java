package org.bibsonomy.webapp.command.actions;

import java.io.Serializable;
import java.util.List;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.webapp.command.SimpleResourceViewCommand;

/**
 * @author nilsraabe
 * @version $Id$
 */
public class UserSamlActivationCommand  extends SimpleResourceViewCommand implements Serializable{
	private static final long serialVersionUID = -4665591098903280881L;

	/**
	 * Fills the news box in the sidebar
	 */
	private List<Post<Bookmark>> news;
	
	/**
	 * Checkbox for activate SAML Account
	 */
	private boolean checkboxAccept;

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
}
