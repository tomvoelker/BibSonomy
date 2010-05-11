package org.bibsonomy.webapp.command.actions;

import java.io.Serializable;

import org.bibsonomy.webapp.command.BaseCommand;

/** 
 * This command encapsulates the user's name and his password for the login page. 
 * 
 * @author rja
 * @version $Id$
 */
public class UserLoginCommand extends BaseCommand implements Serializable {
	private static final long serialVersionUID = -115425449425900599L;
	
	
	/**
	 * The name of the user which wants to login.
	 */
	private String username;
	
	/**
	 * The users password
	 */
	private String password;
	
	/**
	 *	The openID url of the user 
	 */
	private String openID;
	
	/**
	 * URL to which the user wants to jump back after successful login.
	 */
	private String referer;

	/**
	 * Some pages need the user to login first, before they can be used.
	 * They can give the user a notice using this param. 
	 */
	private String notice;
	
	/**
	 * Method for login
	 * 1) Login with bibsonomy usernamae and password
	 *    (if user is a ldap user, password is ldap password, otherwise bibsonomy password) 
	 * 2) Login with LDAP UserID and password 
	 */
	private String loginMethod;
	
	/**
	 * @return the notice
	 */
	public String getNotice() {
		return this.notice;
	}
	
	/**
	 * @param notice the notice to set
	 */
	public void setNotice(String notice) {
		this.notice = notice;
	}
	
	/**
	 * @return the username
	 */
	public String getUsername() {
		return this.username;
	}

	/** 
	 * @param username the username to set (lowercase)
	 */
	public void setUsername(String username) {
		if (username != null) {
			username = username.toLowerCase();
		}
		
		this.username = username;
	}
	
	/**
	 * @return the password
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the referer
	 */
	public String getReferer() {
		return this.referer;
	}

	/**
	 * @param referer the referer to set
	 */
	public void setReferer(String referer) {
		this.referer = referer;
	}
	
	/**
	 * @return the openID
	 */
	public String getOpenID() {
		return this.openID;
	}

	/**
	 * @param openID the openID to set
	 */
	public void setOpenID(String openID) {
		this.openID = openID;
	}

	@Override
	public void setLoginMethod(String loginMethod) {
		this.loginMethod = loginMethod;
	}
	
	@Override
	public String getLoginMethod() {
		return this.loginMethod;
	}
}