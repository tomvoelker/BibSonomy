package org.bibsonomy.webapp.command.actions;

import java.io.Serializable;

import org.bibsonomy.webapp.command.BaseCommand;

/**
 * @author daill
 * @version $Id$
 */
public class PasswordChangeOnRemindCommand extends BaseCommand implements Serializable{

	private String userName;
	private String tmpPassword;
	private String newPassword;
	private String passwordCheck;
	
	public String getUserName() {
		return this.userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getTmpPassword() {
		return this.tmpPassword;
	}
	public void setTmpPassword(String tmpPassword) {
		this.tmpPassword = tmpPassword;
	}
	public String getNewPassword() {
		return this.newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public String getPasswordCheck() {
		return this.passwordCheck;
	}
	public void setPasswordCheck(String passwordCheck) {
		this.passwordCheck = passwordCheck;
	}

	
}
