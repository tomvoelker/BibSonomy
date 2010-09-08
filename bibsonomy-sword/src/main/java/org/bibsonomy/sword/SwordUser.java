package org.bibsonomy.sword;

import static org.bibsonomy.util.ValidationUtils.present;

/**
 * 
 * @author:  sst
 * @version: $Id$
 * $Author$
 * 
 */
public class SwordUser {

	private String username;
	private String apikey;


	public SwordUser (String username, String apikey) {
		this.username = username;
		this.apikey = apikey;
	}

	
	@Override
	public String toString() {
		return username + "-" + apikey;
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
}