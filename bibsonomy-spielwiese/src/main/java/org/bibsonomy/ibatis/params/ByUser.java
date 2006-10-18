package org.bibsonomy.ibatis.params;

import java.util.Date;

public class ByUser {
	private String user;
	private Date registrationDate;

	public ByUser(String user, Date date) {
		super();
		this.user = user;
		this.registrationDate = date;
	}

	public String getUser() {
		return user;
	}

	public ByUser(String user) {
		super();
		this.user = user;
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}
	
}
