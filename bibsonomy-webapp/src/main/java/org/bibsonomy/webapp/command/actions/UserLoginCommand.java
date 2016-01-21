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

import org.bibsonomy.webapp.command.BaseCommand;

/**
 * @author dzo
 */
public class UserLoginCommand extends BaseCommand implements Serializable {

	private static final long serialVersionUID = -8690852609913391454L;
	
	private String message;
	
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
	
	/** preselected tab */
 	private int selTab;
	
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
	 * For users who want to stay logged in for longer time with a cookie
	 */
	private boolean rememberMe;
	
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
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * @return the username
	 */
	public String getUsername() {
		return this.username;
	}

	/** 
	 * @param username the username to set
	 */
	public void setUsername(String username) {
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

	/**
	 * @return If user wants to stay logged in using a cookie.
	 */
	public boolean isRememberMe() {
		return this.rememberMe;
	}

	/**
	 * @param rememberMe
	 */
	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}

	/**
	 * @return the selTab
	 */
	public int getSelTab() {
		return this.selTab;
	}

	/**
	 * @param selTab the selTab to set
	 */
	public void setSelTab(int selTab) {
		this.selTab = selTab;
	}
}
