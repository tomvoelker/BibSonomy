/*
 * Created on 14.10.2007
 */
package org.bibsonomy.webapp.command;

import org.bibsonomy.model.User;

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
	private User loginUser;
	private String pageTitle;
	
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
	public User getLoginUser() {
		return this.loginUser;
	}
	/**
	 * @param loginUser the user, who is currently logged in
	 */
	public void setLoginUser(User loginUser) {
		this.loginUser = loginUser;
	}
}
