package org.bibsonomy.ibatis.params;

import java.util.Date;

public class ByUser {
	private String user;
	private Date registrationDate;

	public ByUser(String user) {
		this.user = user;
	}

	public ByUser(String user, Date date) {
		this(user);
		this.registrationDate = date;
	}

	public String getUser() {
		return user;
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}
}