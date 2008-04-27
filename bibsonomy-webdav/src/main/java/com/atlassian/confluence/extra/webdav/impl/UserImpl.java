package com.atlassian.confluence.extra.webdav.impl;

import com.atlassian.user.User;

import java.security.Principal;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class UserImpl implements Principal, User {

	private final String name;

	/**
	 * Constructs an instance of UserImpl with the given name.
	 * 
	 * @param name
	 */
	public UserImpl(final String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public String getEmail() {
		return null;
	}

	public String getFullName() {
		return this.name;
	}

	public void setEmail(String email) {
	}

	public void setFullName(String fullName) {
	}

	public void setPassword(String password) {
	}
}