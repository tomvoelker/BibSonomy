/*
 * Created on 14.10.2007
 */
package org.bibsonomy.webapp.command;

import beans.UserBean;

/**
 * Base class for command objects. Contains request and response fields
 * that are commonly used across a lot of controllers.
 * 
 * Command objects normally contain 
 * all request arguments that a controller needs and all response values,
 * which it creates. Views use the information in commands for rendering.
 * 
 * @author Jens Illig
 */
public class BaseCommand {
	private String ckey;
	
	private UserBean user;
	private String pageTitle;
	private String requPath;
	private String error;
	
	/**
	 * @return error message
	 */
	public String getError() {
		return this.error;
	}

	/**
	 * @param error an error message
	 */
	public void setError(String error) {
		this.error = error;
	}

	/**
	 * @return the requested path
	 */
	public String getRequPath() {
		return this.requPath;
	}

	/**
	 * @param requPath the requested path
	 */
	public void setRequPath(String requPath) {
		this.requPath = requPath;
	}

	/**
	 * @return the page title
	 */
	public String getPageTitle() {
		return this.pageTitle;
	}

	/**
	 * @param pageTitle the page title
	 */
	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	/**
	 * @return communication key used to save us from request forgery attacks
	 */
	public String getCkey() {
		return this.ckey;
	}
	/**
	 * @param ckey communication key used to save us from request forgery attacks
	 */
	public void setCkey(String ckey) {
		this.ckey = ckey;
	}
	
	/**
	 * @return the user, who is currently logged in
	 */
	public UserBean getUser() {
		return this.user;
	}
	/**
	 * @param loginUser the user, who is currently logged in
	 */
	public void setUser(UserBean loginUser) {
		this.user = loginUser;
	}
	
	/**
	 * helper function to ease checking if a logged in user exists
	 * @return true if a user is logged in, false otherwise
	 */
	public Boolean userLoggedIn() {
		return this.user.getName() != null;
	}
	
	/**
	 * wrapper for function userLoggedIn to enable access from JSPs
	 * @see BaseCommand.userLoggedIn()
	 * @return true if a user is logged in, false otherwise 
	 */
	public Boolean getUserLoggedIn() {
		return this.userLoggedIn();
	}
	
	/**
	 * Helper function to expose the name of the current command
	 * to the JSPs
	 * 
	 * @return the class name of the current command
	 */
	public String getCommandName() {
		return this.getClass().getSimpleName();
	}
}
