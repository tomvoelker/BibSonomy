package org.bibsonomy.database.params;

import java.net.URL;

/**
 * Parameters that are specific to users.
 *
 * @author Miranda Grahl
 * @version $Id$
 */
public class UserParam extends GenericParam {

	private String groupingName;
	private String apiKey;
	private String email;
	private String password;
	private String realname;
	private URL homepage;
	
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public URL getHomepage() {
		return this.homepage;
	}

	public void setHomepage(URL url) {
		this.homepage = url;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRealname() {
		return this.realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getGroupingName() {
		return this.groupingName;
	}

	public void setGroupingName(String groupingName) {
		this.groupingName = groupingName;
	}

	public String getApiKey() {
		return this.apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
}