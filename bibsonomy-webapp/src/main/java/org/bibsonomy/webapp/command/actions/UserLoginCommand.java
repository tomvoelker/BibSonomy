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

	
	/**
	 * 
	 */
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
	 * available methods for authenticatiion/login
	 * comma seperated values, e.g. db,ldap,openid
	 */
	private String authOrder;

	/**
	 * Method for login
	 * 1) Login with bibsonomy usernamae and password
	 *    (if user is a ldap user, password is ldap password, otherwise bibsonomy password) 
	 * 2) Login with LDAP UserID and password 
	 */
	private String loginMethod;

	
	public String getNotice() {
		return this.notice;
	}
	public void setNotice(String notice) {
		this.notice = notice;
	}
	public String getUsername() {
		return this.username;
	}
	public void setUsername(String username) {
		if (username != null) {
			this.username = username.toLowerCase();
		} else {
			this.username = username;
		}
	}
	public String getPassword() {
		return this.password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getReferer() {
		return this.referer;
	}
	public void setReferer(String referer) {
		this.referer = referer;
	}
	public String getOpenID() {
		return this.openID;
	}
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
	public void setAuthOrder(String authOrder) {
		this.authOrder = authOrder;
	}
	public String getAuthOrder() {
		return authOrder;
	}
}