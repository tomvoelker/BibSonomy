package com.atlassian.user;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public interface User {
	public static final String ANONYMOUS = "anonymous";

	public String getEmail();

	public String getFullName();

	public void setEmail(String email);

	public void setFullName(String fullName);

	public void setPassword(String password);
}