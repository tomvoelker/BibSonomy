/*
 * Created on 14.10.2007
 */
package org.bibsonomy.webapp.command;

import org.bibsonomy.model.User;

public class BaseCommand {
	public static final String PROJECT_NAME = "bibsonomy";
	private String ckey;
	private User loginUser;
	private String pageTitle;
	
	public String getPageTitle() {
		return this.pageTitle;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	public String getProjectName() {
		return PROJECT_NAME;
	}

	public String getCkey() {
		return this.ckey;
	}
	public void setCkey(String ckey) {
		this.ckey = ckey;
	}
	
	public User getLoginUser() {
		return this.loginUser;
	}
	public void setLoginUser(User loginUser) {
		this.loginUser = loginUser;
	}
}
