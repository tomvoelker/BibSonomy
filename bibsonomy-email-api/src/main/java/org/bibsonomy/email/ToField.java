package org.bibsonomy.email;

import static org.bibsonomy.util.ValidationUtils.present;

/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class ToField {

	private String username;
	private String apikey;
	private String group;

	@Override
	public String toString() {
		return username + "-" + apikey + (present(group) ? "+" + group : "");
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getApikey() {
		return apikey;
	}
	public void setApikey(String apikey) {
		this.apikey = apikey;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
}